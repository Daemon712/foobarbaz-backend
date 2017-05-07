package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;

public interface UserChallengeDetailsRepository extends CrudRepository<ChallengeUserDetails, ChallengeUserPK> {
}
