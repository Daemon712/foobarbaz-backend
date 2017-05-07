package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.challenge.ChallengeDetails;

public interface ChallengeDetailsRepository extends CrudRepository<ChallengeDetails, Long> {
}
