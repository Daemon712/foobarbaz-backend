package ru.foobarbaz.logic;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.*;
import ru.foobarbaz.exception.TestNotPassedException;
import ru.foobarbaz.repo.*;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private TestService testService;
    private ChallengeRepository challengeRepository;
    private ChallengeStatusRepository statusRepository;
    private ChallengeDetailsRepository detailsRepository;
    private UserAccountRepository userAccountRepository;
    private UserChallengeDetailsRepository userDetailsRepository;

    @Autowired
    public ChallengeServiceImpl(
            TestService testService,
            ChallengeRepository challengeRepository,
            ChallengeStatusRepository statusRepository,
            ChallengeDetailsRepository detailsRepository,
            UserAccountRepository userAccountRepository,
            UserChallengeDetailsRepository userDetailsRepository) {
        this.testService = testService;
        this.challengeRepository = challengeRepository;
        this.statusRepository = statusRepository;
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
        statusRepository.save(challengeStatus);

        Solution solution = new Solution(new SolutionPK(author, id, 1));
        solution.setImplementation(challenge.getDetails().getSample());
        solution.setStatus(SolutionStatus.SUCCESS);
        solution.setTestResults(results);

        UserChallengeDetails userDetails = new UserChallengeDetails(userChallengePK);
        userDetails.setSolutions(Collections.singletonList(solution));
        userDetails.setRating(challenge.getRating());
        userDetails.setDifficulty(challenge.getDifficulty());

        UserAccount userAccount = userAccountRepository.findOne(author)
                .orElseThrow(ResourceNotFoundException::new);
        userAccount.setChallenges(userAccount.getChallenges() + 1);
        userDetails.setUserAccount(userAccount);

        userDetailsRepository.save(userDetails);
    }

    @Override
    public Challenge getChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findOne(challengeId).orElseThrow(ResourceNotFoundException::new);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        ChallengeStatus status = user != null ? statusRepository.findOne(new UserChallengePK(user, challengeId)).orElse(null) : null;
        challenge.setStatus(status != null ? status.getStatus() : ChallengeStatus.NOT_STARTED);
        return challenge;
    }

    @Override
    public ChallengeDetails getChallengeDetails(Long challengeId) {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        UserChallengeDetails userDetails = userDetailsRepository.findOne(new UserChallengePK(user, challengeId)).orElse(null);

        ChallengeDetails details = userDetails != null ?
                userDetails.getChallengeDetails() :
                detailsRepository.findOne(challengeId).orElseThrow(ResourceNotFoundException::new);

        details.setViews(details.getViews() + 1);
        detailsRepository.save(details);

        details.getChallenge().setStatus(userDetails != null ? userDetails.getStatus().getStatus() : ChallengeStatus.NOT_STARTED);

        details.setUserDetails(userDetails);

        return details;
    }

    @Override
    public List<Challenge> getChallenges() {
        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<Long, Integer> statusMap =  user == null ?
                Collections.emptyMap() :
                StreamSupport.stream(statusRepository.findByPkUsername(user).spliterator(), false)
                        .collect(Collectors.toMap(
                                (status) -> status.getPk().getChallengeId(),
                                ChallengeStatus::getStatus
                        ));

        Iterable<Challenge> challenges = challengeRepository.findAll();
        return StreamSupport.stream(challenges.spliterator(), false)
                .peek(c -> c.setDetails(null))
                .peek(c -> c.setStatus(statusMap.getOrDefault(c.getChallengeId(), ChallengeStatus.NOT_STARTED)))
                .collect(Collectors.toList());
    }
}
