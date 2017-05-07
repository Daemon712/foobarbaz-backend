package ru.foobarbaz.web.rest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.challenge.solution.Solution;
import ru.foobarbaz.entity.challenge.solution.SolutionPK;
import ru.foobarbaz.logic.SharedSolutionService;
import ru.foobarbaz.logic.SolutionService;
import ru.foobarbaz.repo.SolutionRepository;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(value = "api/challenges/{challengeId}/solutions")
public class SolutionRestService {
    private SolutionRepository solutionRepository;
    private SharedSolutionService sharedSolutionService;
    private SolutionService solutionService;

    public SolutionRestService(
            SolutionRepository solutionRepository,
            SharedSolutionService sharedSolutionService,
            SolutionService solutionService) {
        this.solutionRepository = solutionRepository;
        this.sharedSolutionService = sharedSolutionService;
        this.solutionService = solutionService;
    }

    @RequestMapping
    public List<Solution> getSolutions(@PathVariable long challengeId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return solutionRepository.findByPkChallengeIdAndPkUsername(challengeId, username);
    }

    @RequestMapping(value = "/new/test", method = RequestMethod.POST)
    public Solution testSolution(@PathVariable long challengeId, @RequestBody String impl){
        Solution solution = buildEntity(challengeId, null, impl);
        return solutionService.testSolution(solution);
    }

    @RequestMapping(value = "/{solutionNum}/test", method = RequestMethod.POST)
    public Solution testSolution(@PathVariable long challengeId,
                                 @PathVariable int solutionNum,
                                 @RequestBody String impl){

        Solution solution = buildEntity(challengeId, solutionNum, impl);
        return solutionService.testSolution(solution);
    }

    @RequestMapping(value = "/new/save", method = RequestMethod.POST)
    public Solution saveSolution(@PathVariable long challengeId, @RequestBody String impl){
        Solution solution = buildEntity(challengeId, null, impl);
        return solutionService.saveSolution(solution);
    }

    @RequestMapping(value = "/{solutionNum}/save", method = RequestMethod.POST)
    public Solution saveSolution(@PathVariable long challengeId,
                                 @PathVariable int solutionNum,
                                 @RequestBody String impl){
        Solution solution = buildEntity(challengeId, solutionNum, impl);
        return solutionService.saveSolution(solution);
    }

    @RequestMapping(value = "/{solutionNum}", method = RequestMethod.DELETE)
    public void deleteSolution(@PathVariable long challengeId, @PathVariable int solutionNum){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        solutionRepository.deleteById(new SolutionPK(username, challengeId, solutionNum));
    }

    @RequestMapping(value = "/{solutionNum}/share", method = RequestMethod.POST)
    public Long shareSolution(
            @PathVariable long challengeId,
            @PathVariable int solutionNum,
            @RequestBody String comment){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        SolutionPK solutionPK = new SolutionPK(username, challengeId, solutionNum);
        return sharedSolutionService.shareSolution(solutionPK, comment).getSharedSolutionId();
    }

    private Solution buildEntity(Long challengeId, Integer solutionNum, String impl){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Solution solution = new Solution(username, challengeId, solutionNum);
        solution.setImplementation(impl);
        return solution;
    }
}
