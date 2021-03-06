package ru.foobarbaz.logic.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus;
import ru.foobarbaz.entity.challenge.solution.Solution;
import ru.foobarbaz.entity.challenge.solution.SolutionPK;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.exception.TestNotPassedException;
import ru.foobarbaz.logic.ChallengeService;
import ru.foobarbaz.logic.TestService;
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

import static ru.foobarbaz.constant.ChallengeRatingConst.MAX_RATING;

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
                .map(TestResult::getStatus)
                .allMatch(s -> s == SolutionStatus.SUCCESS);

        if (!passed) throw new TestNotPassedException(results);
        return results;
    }

    private Challenge save(Challenge challenge, ChallengeDetails details, List<TestResult> results) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();

        challenge.setAuthor(new User(author));
        challenge.setRating(MAX_RATING);
        challenge.setCreated(new Date());
        if (challenge.getTags() != null) {
            Set<String> processedTags = challenge.getTags().stream()
                    .map(String::trim)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
            challenge.setTags(processedTags);
        }
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
        ChallengeUserPK userChallengePK = new ChallengeUserPK(author, id);

        ChallengeUserStatus challengeStatus = new ChallengeUserStatus(userChallengePK);
        challengeStatus.setStatus(ChallengeStatus.SOLVED);

        Solution solution = new Solution(new SolutionPK(author, id, 1));
        solution.setImplementation(challenge.getDetails().getSample());
        solution.setStatus(SolutionStatus.SUCCESS);
        solution.setTestResults(results);

        ChallengeUserRating rating = new ChallengeUserRating(userChallengePK);
        rating.setRating(challenge.getRating());
        rating.setDifficulty(challenge.getDifficulty());

        ChallengeUserDetails userDetails = new ChallengeUserDetails(userChallengePK);
        userDetails.setStatus(challengeStatus);
        userDetails.setSolutions(Collections.singletonList(solution));
        userDetails.setRating(rating);

        UserAccount userAccount = userAccountRepository.findById(author)
                .orElseThrow(ResourceNotFoundException::new);
        userAccount.setChallenges(userAccount.getChallenges() + 1);
        userDetails.setUserAccount(userAccount);

        userDetailsRepository.save(userDetails);
    }

    @Override
    public void updateChallengeBookmark(long challengeId, boolean bookmark) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        ChallengeUserPK pk = new ChallengeUserPK(username, challengeId);
        ChallengeUserDetails userChallengeDetails = new ChallengeUserDetails(pk);
        userChallengeDetails.setBookmark(bookmark);
        userDetailsRepository.save(userChallengeDetails);
    }

    @Override
    public Challenge getChallenge(long challengeId) {
        return challengeRepository.findById(challengeId).orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Challenge getRandomChallenge() {
        long count = challengeRepository.count();
        if (count == 0) return null;
        int index = (int) Math.floor(Math.random() * count);
        Page<Challenge> page = challengeRepository.findAll(PageRequest.of(index, 1));
        fillChallengeStatus(page.getContent());
        return page.getContent().get(0);
    }

    @Override
    public ChallengeDetails getChallengeDetails(long challengeId) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        ChallengeUserDetails userDetails = userDetailsRepository.findById(new ChallengeUserPK(user, challengeId)).orElse(null);

        ChallengeDetails details = userDetails != null
                ? userDetails.getChallengeDetails()
                : detailsRepository.findById(challengeId).orElseThrow(ResourceNotFoundException::new);

        details.setViews(details.getViews() + 1);
        detailsRepository.save(details);

        ChallengeStatus status = userDetails != null && userDetails.getStatus() != null
                ? userDetails.getStatus().getStatus()
                : ChallengeStatus.NOT_STARTED;
        details.getChallenge().setStatus(status);

        details.setUserDetails(userDetails);
        details.getChallenge().setDetails(details);
        return details;
    }

    @Override
    public Challenge updateChallenge(Challenge update) {
        ChallengeDetails details = detailsRepository.findById(update.getChallengeId())
                .orElseThrow(ResourceNotFoundException::new);
        Challenge challenge = details.getChallenge();
        challenge.setName(update.getName());
        challenge.setShortDescription(update.getShortDescription());
        challenge.setTags(update.getTags());
        challenge.setDetails(details);
        details.setFullDescription(update.getDetails().getFullDescription());
        details.setCommentAccess(update.getDetails().getCommentAccess());
        details.setShareAccess(update.getDetails().getShareAccess());
        return challengeRepository.save(challenge);
    }

    @Override
    public void deleteChallenge(long challengeId) {
        challengeRepository.deleteById(challengeId);
    }

    @Override
    public List<Challenge> getChallenges() {
        return challengeRepository.findAll();
    }

    @Override
    public void fillChallengeStatus(List<Challenge> challenges) {
        List<Long> ids = challenges.stream().map(Challenge::getChallengeId).collect(Collectors.toList());
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Set<ChallengeUserStatus> statuses = challengeRepository.findStatusesByIds(username, ids);
        for (Challenge c : challenges) {
            c.setStatus(ChallengeStatus.NOT_STARTED);
            for (ChallengeUserStatus s : statuses) {
                if (c.getChallengeId() == s.getPk().getChallengeId()){
                    c.setStatus(s.getStatus());
                    break;
                }
            }
        }
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

    @Override
    public Page<Challenge> getChallenges(Pageable pageable, ChallengeRepository.ChallengeFilter filter) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return challengeRepository.findAllWithStatus(username, filter, pageable);
    }

    @Override
    public List<Challenge> quickSearchChallenges(String name) {
        return name == null
                ? challengeRepository.findTop10By()
                : challengeRepository.findTop10ByNameContainsIgnoreCase(name);
    }
}
