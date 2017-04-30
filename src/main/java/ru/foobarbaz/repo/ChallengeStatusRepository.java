package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.ChallengeStatus;
import ru.foobarbaz.entity.UserChallengePK;

public interface ChallengeStatusRepository extends CrudRepository<ChallengeStatus, UserChallengePK> {
    Iterable<ChallengeStatus> findByPkUsername(String username);
}
