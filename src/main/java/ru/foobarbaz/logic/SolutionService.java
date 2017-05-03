package ru.foobarbaz.logic;

import ru.foobarbaz.entity.Solution;

public interface SolutionService {
    Solution saveSolution(Solution template);
    Solution testSolution(Solution template);
}
