package ru.foobarbaz.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.logic.SharedSolutionService;
import ru.foobarbaz.web.view.SharedSolutionView;

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
    @JsonView(SharedSolutionView.ChallengeInfo.class)
    public List<SharedSolution> getSolutionByUser(@PathVariable String username){
        return service.getSolutionsByUser(username);
    }

    @RequestMapping("/challenge/{challengeId}")
    @JsonView(SharedSolutionView.Author.class)
    public List<SharedSolution> getSolutionByChallenge(@PathVariable Long challengeId){
        return service.getSolutionsByChallenge(challengeId);
    }

    @RequestMapping("/{sharedSolutionId}")
    @JsonView(SharedSolutionView.Full.class)
    public SharedSolution getSharedSolution(@PathVariable Long sharedSolutionId){
        return service.getSharedSolution(sharedSolutionId);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{sharedSolutionId}/like", method = RequestMethod.POST)
    public int updateLike(
            @PathVariable Long sharedSolutionId,
            @RequestBody String like) {
        return service.updateLike(sharedSolutionId, Boolean.valueOf(like)).getLikes().size();
    }
}
