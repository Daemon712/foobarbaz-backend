package ru.foobarbaz.logic;

import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.challenge.solution.SolutionPK;

import java.util.List;

public interface SharedSolutionService {
    SharedSolution shareSolution(SolutionPK solutionPK, String comment);
    List<SharedSolution> getSolutionsByUser(String username);
    List<SharedSolution> getSolutionsByChallenge(long challengeId);
    SharedSolution getSharedSolution(long sharedSolutionId);
    void updateLike(long sharedSolutionId, boolean like);
}
