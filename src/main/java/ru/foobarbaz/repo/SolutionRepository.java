package ru.foobarbaz.repo;

import org.springframework.data.repository.CrudRepository;
import ru.foobarbaz.entity.Solution;
import ru.foobarbaz.entity.SolutionPK;

import java.util.List;

public interface SolutionRepository extends CrudRepository<Solution, SolutionPK> {
    List<Solution> findByPkChallengeIdAndPkUsername(long challengeId, String username);
}
