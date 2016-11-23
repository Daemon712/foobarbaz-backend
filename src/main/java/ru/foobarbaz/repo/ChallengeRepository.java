package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.Challenge;

public interface ChallengeRepository extends CrudRepository<Challenge, Long> {
}
