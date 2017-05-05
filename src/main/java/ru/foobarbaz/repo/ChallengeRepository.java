package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.TagStatistic;

import java.util.List;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    @Query("select new ru.foobarbaz.entity.TagStatistic(t, count(c)) " +
            "from Challenge c " +
            "inner join c.tags t " +
            "group by t " +
            "order by count(c) ")
    List<TagStatistic> findAllTags();

    @Query("select new ru.foobarbaz.entity.TagStatistic(t, count(c)) " +
            "from Challenge c " +
            "inner join c.tags t " +
            "where t like concat('%', lower(:tag), '%') " +
            "group by t " +
            "order by count(c) ")
    List<TagStatistic> searchTags(@Param("tag") String tag);
}
