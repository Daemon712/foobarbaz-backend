package ru.foobarbaz.logic;

import ru.foobarbaz.entity.Solution;
import ru.foobarbaz.entity.TestResult;

import java.util.List;

public interface SolutionService {
    Solution createSolution(long challengeId, String implementation);
    List<TestResult> testSolution(long solutionId, String implementation);
    Solution updateSolution(long solutionId, String implementation);
}
