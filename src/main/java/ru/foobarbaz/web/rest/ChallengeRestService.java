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
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.exception.TestNotPassedException;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.logic.TestService;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.web.dto.NewChallenge;
import ru.foobarbaz.web.dto.TestRequest;
import ru.foobarbaz.web.dto.UpdateChallenge;
import ru.foobarbaz.web.dto.UpdateRating;
import ru.foobarbaz.web.view.ChallengeView;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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

    @PreAuthorize("isAuthenticated() && hasPermission(#challengeId, 'Challenge', 'modify')")
    @PutMapping("{challengeId}")
    public Challenge updateChallenge(@PathVariable long challengeId, @Valid @RequestBody UpdateChallenge input){
        Challenge challenge = new Challenge();
        challenge.setChallengeId(challengeId);
        BeanUtils.copyProperties(input, challenge);

        ChallengeDetails details = new ChallengeDetails();
        BeanUtils.copyProperties(input, details);
        challenge.setDetails(details);

        return challengeService.updateChallenge(challenge);
    }

    @PreAuthorize("isAuthenticated() && hasPermission(#challengeId, 'Challenge', 'modify')")
    @DeleteMapping("{challengeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChallenge(@PathVariable long challengeId){
        challengeService.deleteChallenge(challengeId);
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

    @GetMapping(path = "random")
    @JsonView(ChallengeView.Short.class)
    public Challenge getRandomChallenge(){
        return challengeService.getRandomChallenge();
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
            @RequestParam(required = false, defaultValue = "desc") String dir,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String tag
    ){
        Sort sort = Sort.by(Sort.Direction.fromString(dir), field);
        PageRequest pageable = PageRequest.of(page, 5, sort);
        if (Stream.of(name, rating, difficulty, status, tag).allMatch(Objects::isNull)){
            return challengeService.getChallenges(pageable);
        } else {
            if (name == null) name = "";
            ChallengeStatus challengeStatus = status == null ? null : ChallengeStatus.values()[status];
            ChallengeRepository.ChallengeFilter filter =
                    new ChallengeRepository.ChallengeFilter(name, tag, challengeStatus, rating, difficulty);
            return challengeService.getChallenges(pageable, filter);
        }
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
    @RequestMapping("/quick-search")
    public List<Challenge> quickSearch(@RequestParam(required = false) String name){
        return challengeService.quickSearchChallenges(name);
    }
}
