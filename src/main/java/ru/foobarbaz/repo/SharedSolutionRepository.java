package ru.foobarbaz.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.user.User;

import java.util.List;

public interface SharedSolutionRepository extends JpaRepository<SharedSolution, Long> {
    List<SharedSolution> findAllByChallengeDetailsOrderByCreated(ChallengeDetails challengeId);
    List<SharedSolution> findAllByAuthorOrderByCreated(User author);
}
