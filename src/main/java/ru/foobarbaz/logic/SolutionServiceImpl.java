package ru.foobarbaz.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.Solution;
import ru.foobarbaz.entity.TestResult;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.repo.SolutionRepository;

import java.util.Collections;
import java.util.List;

@Service
public class SolutionServiceImpl implements SolutionService {
    private SolutionRepository solutionRepository;
    private String solutionNameTemplate = "Решение №%s";//TODO

    @Autowired
    public SolutionServiceImpl(SolutionRepository solutionRepository) {
        this.solutionRepository = solutionRepository;
    }

    @Override
    public Solution createSolution(long challengeId, String implementation) {
        Solution solution = new Solution();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        solution.setUser(new User(username));
        solution.setChallenge(new Challenge(challengeId));
        Long solutionNumber = solutionRepository.countByChallengeAndUser(new Challenge(challengeId), new User(username));
        solution.setName(String.format(solutionNameTemplate, solutionNumber));
        solution.setImplementation(implementation);
        return solutionRepository.save(solution);
    }

    @Override
    public List<TestResult> testSolution(long solutionId, String implementation) {
        Solution solution = solutionRepository.findOne(solutionId).orElseThrow(ResourceNotFoundException::new);
        solution.setCompleted((int)(Math.random() * 100));//TODO
        solution.setImplementation(implementation);
        solutionRepository.save(solution);
        return Collections.emptyList();
    }

    @Override
    public Solution updateSolution(long solutionId, String implementation) {
        Solution solution = solutionRepository.findOne(solutionId).orElseThrow(ResourceNotFoundException::new);
        solution.setImplementation(implementation);
        return solutionRepository.save(solution);
    }
}
