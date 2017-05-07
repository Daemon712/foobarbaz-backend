package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.foobarbaz.entity.UserChallengePK;
import ru.foobarbaz.entity.ChallengeRating;

public interface UserChallengeRatingRepository extends JpaRepository<ChallengeRating, UserChallengePK> {
    @Query( "select new ru.foobarbaz.entity.ChallengeRating(avg(r.rating), avg(r.difficulty)) " +
            "from ChallengeRating r " +
            "where r.pk.challengeId = :challengeId " +
            "group by r.pk.challengeId")
    ChallengeRating calcAvgRating(@Param("challengeId") long challengeId);
}
