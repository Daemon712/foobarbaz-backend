package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;

public interface UserChallengeRatingRepository extends JpaRepository<ChallengeUserRating, ChallengeUserPK> {
    @Query( "select new ru.foobarbaz.entity.challenge.personal.ChallengeUserRating(avg(r.rating), avg(r.difficulty)) " +
            "from ChallengeUserRating r " +
            "where r.pk.challengeId = :challengeId " +
            "group by r.pk.challengeId")
    ChallengeUserRating calcAvgRating(@Param("challengeId") long challengeId);
}
