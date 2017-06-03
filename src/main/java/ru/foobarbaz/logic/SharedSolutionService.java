package ru.foobarbaz.logic;

import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.challenge.solution.SolutionPK;

import java.util.List;

public interface SharedSolutionService {
    SharedSolution shareSolution(SolutionPK solutionPK, String comment);
    List<SharedSolution> getSolutionsByUser(String username);
    List<SharedSolution> getSolutionsByChallenge(long challengeId);
    SharedSolution getSharedSolution(long sharedSolutionId);
    SharedSolution updateSolution(SharedSolution sharedSolution);
    void deleteSolution(long sharedSolutionId);
    SharedSolution updateLike(long sharedSolutionId, boolean like);
}
