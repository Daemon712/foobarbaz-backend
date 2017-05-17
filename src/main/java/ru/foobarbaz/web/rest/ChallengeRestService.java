package ru.foobarbaz.web.rest;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.exception.TestNotPassedException;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.logic.TestService;
import ru.foobarbaz.web.dto.NewChallenge;
import ru.foobarbaz.web.dto.TestRequest;
import ru.foobarbaz.web.dto.UpdateRating;
import ru.foobarbaz.web.view.ChallengeView;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/challenges")
public class ChallengeRestService {
    private ChallengeService challengeService;
    private RatingService ratingService;
    private TestService testService;

    @Autowired
    public ChallengeRestService(ChallengeService challengeService, RatingService ratingService, TestService testService) {
        this.challengeService = challengeService;
        this.ratingService = ratingService;
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
    public List<TestResult> testNewChallenge(@Valid @RequestBody TestRequest input){
        return testService.executeTests(input.getTest(), input.getCode());
    }

    @JsonView(ChallengeView.Full.class)
    @RequestMapping(value = "/{challengeId}", method = RequestMethod.GET)
    public Challenge getChallenge(@PathVariable Long challengeId){
        return challengeService.getChallengeDetails(challengeId).getChallenge();
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{challengeId}/bookmark", method = RequestMethod.POST)
    public void updateChallengeBookmark(
            @PathVariable Long challengeId,
            @RequestBody String bookmark){
        challengeService.updateChallengeBookmark(challengeId, Boolean.valueOf(bookmark));
    }

    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/{challengeId}/rating", method = RequestMethod.POST)
    public ChallengeUserRating updateChallengeRating(
            @PathVariable Long challengeId,
            @Valid @RequestBody UpdateRating input){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ChallengeUserRating rating = new ChallengeUserRating();
        rating.setPk(new ChallengeUserPK(username, challengeId));
        rating.setRating(input.getRating());
        rating.setDifficulty(input.getDifficulty());
        return ratingService.updateChallengeRating(rating);
    }

    @JsonView(ChallengeView.Short.class)
    @RequestMapping
    public Page<Challenge> getChallenges(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "created") String field,
            @RequestParam(required = false, defaultValue = "desc") String dir
    ){
        Sort sort = Sort.by(Sort.Direction.fromString(dir), field);
        PageRequest pageable = PageRequest.of(page, 10, sort);
        return challengeService.getChallenges(pageable);
    }

    @JsonView(ChallengeView.Short.class)
    @RequestMapping("/author/{username}")
    public List<Challenge> getChallengesByAuthor(@PathVariable String username){
        return challengeService.getChallengesByAuthor(username);
    }

    @JsonView(ChallengeView.Short.class)
    @RequestMapping("/bookmark/{username}")
    public List<Challenge> getBookmarkedChallenges(@PathVariable String username){
        return challengeService.getBookmarkedChallenges(username);
    }

    @JsonView(ChallengeView.Description.class)
    @RequestMapping("/quick-search/{name}")
    public List<Challenge> quickSearch(@PathVariable String name){
        return challengeService.quickSearchChallenges(name);
    }
}
