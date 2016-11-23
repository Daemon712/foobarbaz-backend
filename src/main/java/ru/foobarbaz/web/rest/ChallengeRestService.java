package ru.foobarbaz.web.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.web.dto.NewChallenge;

import javax.validation.Valid;

@RestController
@RequestMapping("/challenge")
public class ChallengeRestService {
    private ChallengeRepository challengeRepository;
    private ChallengeService challengeService;

    @Autowired
    public ChallengeRestService(ChallengeRepository challengeRepository, ChallengeService challengeService) {
        this.challengeRepository = challengeRepository;
        this.challengeService = challengeService;
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public ResponseEntity<?> createChallenge(@Valid @RequestBody NewChallenge challenge){
        Challenge template = new Challenge();
        BeanUtils.copyProperties(challenge, template);
        Challenge newChallenge = challengeService.createChallenge(template);
        return new ResponseEntity<>(newChallenge.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Challenge getChallenge(@PathVariable("id") Long id){
        return challengeService.getChallenge(id);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Iterable<Challenge> getChallenges(){
        return challengeService.getChallenges();
    }
}
