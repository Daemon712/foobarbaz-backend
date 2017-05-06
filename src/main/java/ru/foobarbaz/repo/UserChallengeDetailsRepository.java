package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.foobarbaz.entity.Rating;
import ru.foobarbaz.entity.UserChallengeDetails;
import ru.foobarbaz.entity.UserChallengePK;

public interface UserChallengeDetailsRepository extends CrudRepository<UserChallengeDetails, UserChallengePK> {
    @Query("select new ru.foobarbaz.entity.Rating(avg(ucd.rating), avg(ucd.difficulty)) " +
            "from UserChallengeDetails ucd " +
            "where ucd.pk.challengeId = :challengeId " +
            "group by ucd.pk.challengeId")
    Rating calcAvgRating(@Param("challengeId") long challengeId);
}
