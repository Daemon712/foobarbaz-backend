package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.challenge.personal.UserChallengeDetails;
import ru.foobarbaz.entity.challenge.personal.UserChallengePK;

public interface UserChallengeDetailsRepository extends CrudRepository<UserChallengeDetails, UserChallengePK> {
}
