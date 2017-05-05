package ru.foobarbaz.web.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeDetails;
import ru.foobarbaz.entity.TestResult;
import ru.foobarbaz.exception.TestNotPassedException;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.logic.TestService;
import ru.foobarbaz.web.dto.NewChallenge;
import ru.foobarbaz.web.dto.TestChallenge;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/challenges")
public class ChallengeRestService {
    private ChallengeService challengeService;
    private TestService testService;

    @Autowired
    public ChallengeRestService(ChallengeService challengeService, TestService testService) {
        this.challengeService = challengeService;
        this.testService = testService;
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

    @ExceptionHandler(TestNotPassedException.class)
    public ResponseEntity<List<TestResult>> testNotPassed(TestNotPassedException e) {
        return new ResponseEntity<>(e.getTestResultList(), HttpStatus.ACCEPTED);
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public List<TestResult> testNewChallenge(@Valid @RequestBody TestChallenge input){
        return testService.executeTests(input.getUnitTest(), input.getSample());
    }

    @RequestMapping(value = "/{challengeId}", method = RequestMethod.GET)
    public Challenge getChallenge(@PathVariable Long challengeId){
        return challengeService.getChallengeDetails(challengeId).getChallenge();
    }

    @RequestMapping
    public Iterable<Challenge> getChallenges(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "created") String field,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ){
        Sort sort = Sort.by(Sort.Direction.fromString(dir), field);
        PageRequest pageable = PageRequest.of(page, 10, sort);
        Page<Challenge> challenges = challengeService.getChallenges(pageable);
        challenges.getContent().forEach(c -> c.setDetails(null));
        return challenges;
    }
}
