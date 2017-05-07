package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.UserChallengeDetails;
import ru.foobarbaz.entity.UserChallengePK;

public interface UserChallengeDetailsRepository extends CrudRepository<UserChallengeDetails, UserChallengePK> {
}
