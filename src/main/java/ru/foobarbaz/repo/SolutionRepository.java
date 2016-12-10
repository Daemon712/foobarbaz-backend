package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.Solution;
import ru.foobarbaz.entity.User;

import java.util.List;

public interface SolutionRepository extends CrudRepository<Solution, Long> {
    List<Solution> findByChallengeAndUser(Challenge challenge, User user);
    Long countByChallengeAndUser(Challenge challenge, User user);
}
