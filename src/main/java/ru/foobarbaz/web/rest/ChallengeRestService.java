package ru.foobarbaz.web.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.web.dto.NewChallenge;

import javax.validation.Valid;

@RestController
@RequestMapping("api/challenges")
public class ChallengeRestService {
    private ChallengeService challengeService;

    @Autowired
    public ChallengeRestService(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createChallenge(@Valid @RequestBody NewChallenge challenge){
        Challenge template = new Challenge();
        BeanUtils.copyProperties(challenge, template);
        Challenge newChallenge = challengeService.createChallenge(template);
        return new ResponseEntity<>(newChallenge.getId(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Challenge getChallenge(@PathVariable Long id){
        return challengeService.getChallenge(id);
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Challenge> getChallenges(){
        return challengeService.getChallenges();
    }
}
