package ru.foobarbaz.logic;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.*;
import ru.foobarbaz.exception.TestNotPassedException;
import ru.foobarbaz.repo.ChallengeDetailsRepository;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserChallengeDetailsRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.foobarbaz.NumberUtils.intOrDefault;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private TestService testService;
    private ChallengeRepository challengeRepository;
    private ChallengeDetailsRepository detailsRepository;
    private UserAccountRepository userAccountRepository;
    private UserChallengeDetailsRepository userDetailsRepository;

    @Autowired
    public ChallengeServiceImpl(
            TestService testService,
            ChallengeRepository challengeRepository,
            ChallengeDetailsRepository detailsRepository,
            UserAccountRepository userAccountRepository,
            UserChallengeDetailsRepository userDetailsRepository) {
        this.testService = testService;
        this.challengeRepository = challengeRepository;
        this.detailsRepository = detailsRepository;
        this.userAccountRepository = userAccountRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    @Transactional
    public Challenge createChallenge(Challenge template) {
        Challenge challenge = new Challenge();
        BeanUtils.copyProperties(template, challenge);
        ChallengeDetails details = new ChallengeDetails();
        BeanUtils.copyProperties(template.getDetails(), details);
        List<TestResult> results = test(details);
        return save(challenge, details, results);
    }

    private List<TestResult> test(ChallengeDetails details) {
        List<TestResult> results = testService.executeTests(details.getUnitTest(), details.getSample());
        boolean passed = results.size() > 0 && results.stream()
                .mapToInt(TestResult::getStatus)
                .allMatch(s -> s == SolutionStatus.SUCCESS);

        if (!passed) throw new TestNotPassedException(results);
        return results;
    }

    private Challenge save(Challenge challenge, ChallengeDetails details, List<TestResult> results) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();

        challenge.setAuthor(new User(author));
        challenge.setRating(Challenge.MAX_RATING);
        challenge.setCreated(new Date());
        Set<String> processedTags = challenge.getTags().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
        challenge.setTags(processedTags);
        challenge.setDetails(null);
        Challenge savedChallenge = challengeRepository.save(challenge);

        details.setChallenge(savedChallenge);
        ChallengeDetails savedDetails = detailsRepository.save(details);
        challenge.setDetails(savedDetails);

        saveSolution(savedChallenge, results);
        return savedChallenge;
    }

    private void saveSolution(Challenge challenge, List<TestResult> results) {
        String author = challenge.getAuthor().getUsername();
        Long id = challenge.getChallengeId();
        UserChallengePK userChallengePK = new UserChallengePK(author, id);

        ChallengeStatus challengeStatus = new ChallengeStatus(userChallengePK);
        challengeStatus.setStatus(ChallengeStatus.SOLVED);

        Solution solution = new Solution(new SolutionPK(author, id, 1));
        solution.setImplementation(challenge.getDetails().getSample());
        solution.setStatus(SolutionStatus.SUCCESS);
        solution.setTestResults(results);

        UserChallengeDetails userDetails = new UserChallengeDetails(userChallengePK);
        userDetails.setStatus(challengeStatus);
        userDetails.setSolutions(Collections.singletonList(solution));
        userDetails.setRating(challenge.getRating());
        userDetails.setDifficulty(challenge.getDifficulty());

        UserAccount userAccount = userAccountRepository.findById(author)
                .orElseThrow(ResourceNotFoundException::new);
        userAccount.setChallenges(userAccount.getChallenges() + 1);
        userDetails.setUserAccount(userAccount);

        userDetailsRepository.save(userDetails);
    }

    @Override
    public void updateChallengeBookmark(Long challengeId, boolean bookmark) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserChallengePK pk = new UserChallengePK(username, challengeId);
        UserChallengeDetails userChallengeDetails = userDetailsRepository.findById(pk)
                .orElse(new UserChallengeDetails(pk));
        userChallengeDetails.setBookmark(bookmark);
        userDetailsRepository.save(userChallengeDetails);
    }

    @Override
    @Transactional
    public Rating updateChallengeRating(Long challengeId, Rating rating) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(ResourceNotFoundException::new);

        Rating oldUserRating = updateUserChallengeRating(challengeId, username, rating);
        if (!username.equals(challenge.getAuthor().getUsername()))
            updateUserAccountRating(challenge.getAuthor().getUsername(), oldUserRating, rating);

        Rating newRating = userDetailsRepository.calcAvgRating(challengeId);
        challenge.setRating(newRating.getRating());
        challenge.setDifficulty(newRating.getDifficulty());
        challengeRepository.save(challenge);
        return newRating;
    }

    private Rating updateUserChallengeRating(Long challengeId, String username, Rating rating) {
        UserChallengePK pk = new UserChallengePK(username, challengeId);
        UserChallengeDetails userChallengeDetails = userDetailsRepository.findById(pk)
                .orElse(new UserChallengeDetails(pk));

        Rating old = new Rating();
        old.setRating(intOrDefault(userChallengeDetails.getRating(), 0));
        old.setDifficulty(intOrDefault(userChallengeDetails.getRating(), 0));

        userChallengeDetails.setRating(rating.getRating());
        userChallengeDetails.setDifficulty(rating.getDifficulty());
        userDetailsRepository.save(userChallengeDetails);

        return old;
    }

    private void updateUserAccountRating(String username, Rating oldRating, Rating newRating) {
        UserAccount account = userAccountRepository.findById(username).orElseThrow(ResourceNotFoundException::new);
        int calculatedRating  = account.getRating() - convertRating(oldRating) + convertRating(newRating);
        account.setRating(calculatedRating);
        userAccountRepository.save(account);
    }

    private static int convertRating(Rating challengeRating){
        int r = challengeRating.getRating();
        return (int) Math.floor(r * r / 3);
    }

    @Override
    public Challenge getChallenge(Long challengeId) {
        return challengeRepository.findById(challengeId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public ChallengeDetails getChallengeDetails(Long challengeId) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        UserChallengeDetails userDetails = userDetailsRepository.findById(new UserChallengePK(user, challengeId)).orElse(null);

        ChallengeDetails details = userDetails != null ?
                userDetails.getChallengeDetails() :
                detailsRepository.findById(challengeId).orElseThrow(ResourceNotFoundException::new);

        details.setViews(details.getViews() + 1);
        detailsRepository.save(details);

        int status = userDetails != null && userDetails.getStatus() != null
                ? userDetails.getStatus().getStatus()
                : ChallengeStatus.NOT_STARTED;
        details.getChallenge().setStatus(status);

        details.setUserDetails(userDetails);
        details.getChallenge().setDetails(details);
        return details;
    }

    @Override
    public List<Challenge> getChallenges() {
        return challengeRepository.findAll();
    }

    @Override
    public List<Challenge> getChallengesByAuthor(String author) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return challengeRepository.findByAuthorWithStatus(username, author);
    }

    @Override
    public List<Challenge> getBookmarkedChallenges(String owner) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return challengeRepository.findBookmarksWithStatus(username, owner);
    }

    @Override
    public Page<Challenge> getChallenges(Pageable pageable) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return challengeRepository.findAllWithStatus(username, pageable);
    }
}
