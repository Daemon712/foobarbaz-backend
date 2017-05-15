package ru.foobarbaz.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.TagStatistic;

import java.util.List;

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
}
