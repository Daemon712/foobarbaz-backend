package ru.foobarbaz.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.logic.SharedSolutionService;

import java.util.List;

@RestController
@RequestMapping("api/shared-solutions/")
public class SharedSolutionRestService {
    private SharedSolutionService service;

    @Autowired
    public SharedSolutionRestService(SharedSolutionService service) {
        this.service = service;
    }

    @RequestMapping("/user/{username}")
    public List<SharedSolution> getSolutionByUser(@PathVariable String username){
        return service.getSolutionsByUser(username);
    }

    @RequestMapping("/challenge/{challengeId}")
    public List<SharedSolution> getSolutionByChallenge(@PathVariable Long challengeId){
        return service.getSolutionsByChallenge(challengeId);
    }

    @RequestMapping("/{sharedSolutionId}")
    public SharedSolution getSharedSolution(@PathVariable Long sharedSolutionId){
        return service.getSharedSolution(sharedSolutionId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{sharedSolutionId}", method = RequestMethod.POST)
    public void updateLike(
            @PathVariable Long sharedSolutionId,
            @RequestBody String like) {
        service.updateLike(sharedSolutionId, Boolean.valueOf(like));
    }
}
