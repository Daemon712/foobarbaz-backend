package ru.foobarbaz.web.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.Solution;
import ru.foobarbaz.entity.TestResult;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.logic.SolutionService;
import ru.foobarbaz.repo.SolutionRepository;

import java.util.List;

@RestController
@RequestMapping
public class SolutionRestService {
    private SolutionRepository solutionRepository;
    private SolutionService solutionService;

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "challenge/{challengeId}/test_solution", method = RequestMethod.POST)
    public List<TestResult> testSolution(@PathVariable long challengeId, @RequestBody String impl){
        return solutionService.testSolution(challengeId, impl);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "challenge/{challengeId}/save_solution", method = RequestMethod.POST)
    public Solution createSolution(@PathVariable long challengeId, @RequestBody String impl){
        return solutionService.createSolution(challengeId, impl);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "challenge/{challengeId}/solutions", method = RequestMethod.GET)
    public List<Solution> getSolutions(@PathVariable long challengeId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = new User(username);
        Challenge challenge = new Challenge(challengeId);
        return solutionRepository.findByChallengeAndUser(challenge, user);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "solution/{solutionId}", method = RequestMethod.GET)
    public Solution getSolution(@PathVariable long solutionId){
        return solutionRepository.findOne(solutionId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "solution/{solutionId}", method = RequestMethod.PUT)
    public Solution updateSolution(@PathVariable long solutionId, @RequestBody String impl){
        return solutionService.updateSolution(solutionId, impl);
    }

}
