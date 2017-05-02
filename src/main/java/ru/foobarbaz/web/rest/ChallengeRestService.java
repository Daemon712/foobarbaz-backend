package ru.foobarbaz.web.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeDetails;
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
    public ResponseEntity<?> createChallenge(@Valid @RequestBody NewChallenge input){
        Challenge challenge = new Challenge();
        BeanUtils.copyProperties(input, challenge);

        ChallengeDetails details = new ChallengeDetails();
        BeanUtils.copyProperties(input, details);
        challenge.setDetails(details);

        Challenge newChallenge = challengeService.createChallenge(challenge);
        return new ResponseEntity<>(newChallenge.getChallengeId(), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{challengeId}", method = RequestMethod.GET)
    public Challenge getChallenge(@PathVariable Long challengeId){
        return challengeService.getChallengeDetails(challengeId).getChallenge();
    }

    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Challenge> getChallenges(){
        return challengeService.getChallenges();
    }
}
