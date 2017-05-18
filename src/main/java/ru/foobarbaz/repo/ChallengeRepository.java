package ru.foobarbaz.repo;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.TagStatistic;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query( "select new ru.foobarbaz.entity.challenge.Challenge(c, s) " +
            "from Challenge c " +
            "left join ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus s " +
            "on c.challengeId = s.pk.challengeId " +
            "and s.pk.username = :username")
    Page<Challenge> findAllWithStatus(@Param("username") String username, Pageable pageable);

    @Query( "select new ru.foobarbaz.entity.challenge.Challenge(c, s) " +
            "from Challenge c " +
            "left join ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus s " +
            "on c.challengeId = s.pk.challengeId " +
            "and s.pk.username = ?#{[0]} " +
            "where lower(c.name) like concat('%', ?#{[1].name}, '%') " +
            "and (?#{[1].rating} is null or c.rating = ?#{[1].rating}) " +
            "and (?#{[1].difficulty} is null or c.difficulty = ?#{[1].difficulty}) " +
            "and (?#{[1].tag} is null or ?#{[1].tag} MEMBER OF c.tags) " +
            "and (?#{[1].status} is null or coalesce(s.status, 0) = ?#{[1].status})")
    Page<Challenge> findAllWithStatus(String username, ChallengeFilter filter, Pageable pageable);

    @Query( "select s " +
            "from ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus s " +
            "where s.pk.username = :username " +
            "and s.pk.challengeId in (:ids)")
    Set<ChallengeUserStatus> findStatusesByIds(@Param("username") String username, @Param("ids") Collection<Long> ids);

    @Query( "select new ru.foobarbaz.entity.challenge.Challenge(c, s) " +
            "from Challenge c " +
            "left join ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus s " +
            "on c.challengeId = s.pk.challengeId " +
            "and s.pk.username = :username " +
            "where c.author.username = :author")
    List<Challenge> findByAuthorWithStatus(@Param("username") String username, @Param("author") String author);

    @Query( "select new ru.foobarbaz.entity.challenge.Challenge(c, s) " +
            "from Challenge c " +
            "inner join ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails ucd " +
            "on c.challengeId = ucd.pk.challengeId " +
            "and ucd.pk.username = :owner " +
            "and ucd.bookmark = true " +
            "left join ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus s " +
            "on c.challengeId = s.pk.challengeId " +
            "and s.pk.username = :username")
    List<Challenge> findBookmarksWithStatus(@Param("username") String username, @Param("owner") String owner);

    @Query( "select new ru.foobarbaz.entity.challenge.TagStatistic(t, count(c)) " +
            "from Challenge c " +
            "inner join c.tags t " +
            "group by t " +
            "order by count(c) desc")
    List<TagStatistic> findAllTags();

    @Query( "select new ru.foobarbaz.entity.challenge.TagStatistic(t, count(c)) " +
            "from Challenge c " +
            "inner join c.tags t " +
            "where t like concat('%', lower(:tag), '%') " +
            "group by t " +
            "order by count(c) desc")
    List<TagStatistic> searchTags(@Param("tag") String tag);

    List<Challenge> findTop10ByNameContainsIgnoreCase(String name);
    List<Challenge> findTop10By();

    class ChallengeFilter {
        public String name;
        public String tag;
        public ChallengeStatus status;
        public Integer rating;
        public Integer difficulty;

        public ChallengeFilter(String name, String tag, ChallengeStatus status, Integer rating, Integer difficulty) {
            this.name = name;
            this.tag = tag;
            this.status = status;
            this.rating = rating;
            this.difficulty = difficulty;
        }
    }
}
