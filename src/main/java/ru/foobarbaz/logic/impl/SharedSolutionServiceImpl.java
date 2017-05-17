package ru.foobarbaz.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.AccessOption;
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.entity.challenge.solution.SharedSolution;
import ru.foobarbaz.entity.challenge.solution.Solution;
import ru.foobarbaz.entity.challenge.solution.SolutionPK;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.exception.DeniedOperationException;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.logic.SharedSolutionService;
import ru.foobarbaz.repo.SharedSolutionRepository;
import ru.foobarbaz.repo.SolutionRepository;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserChallengeDetailsRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SharedSolutionServiceImpl implements SharedSolutionService {
    private UserChallengeDetailsRepository userChallengeDetailsRepository;
    private SolutionRepository solutionRepository;
    private SharedSolutionRepository sharedSolutionRepository;
    private RatingService ratingService;
    private UserAccountRepository userAccountRepository;

    @Autowired
    public SharedSolutionServiceImpl(UserChallengeDetailsRepository userChallengeDetailsRepository,
                                     SolutionRepository solutionRepository,
                                     SharedSolutionRepository sharedSolutionRepository,
                                     RatingService ratingService,
                                     UserAccountRepository userAccountRepository) {
        this.userChallengeDetailsRepository = userChallengeDetailsRepository;
        this.solutionRepository = solutionRepository;
        this.sharedSolutionRepository = sharedSolutionRepository;
        this.ratingService = ratingService;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public SharedSolution shareSolution(SolutionPK solutionPK, String comment) {
        Solution solution = solutionRepository.findById(solutionPK).orElseThrow(ResourceNotFoundException::new);

        if (solution.getHolder().getChallengeDetails().getShareAccess() == AccessOption.DENY){
            throw new DeniedOperationException("the operation is denied by challenge settings");
        }
        if (solution.getHolder().getChallengeDetails().getShareAccess() == AccessOption.SOLVED_ONLY &&
                !challengeSolved(solution.getHolder())){
            throw new DeniedOperationException("the operation will be allowed after the challenge is solved");
        }

        UserAccount userAccount = solution.getHolder().getUserAccount();
        userAccount.setSharedSolutions(userAccount.getSharedSolutions() + 1);
        userAccountRepository.save(userAccount);

        SharedSolution sharedSolution = new SharedSolution();
        sharedSolution.setChallengeDetails(new ChallengeDetails(solutionPK.getChallengeId()));
        sharedSolution.setAuthor(userAccount.getUser());
        sharedSolution.setStatus(solution.getStatus());
        sharedSolution.setImplementation(solution.getImplementation());
        sharedSolution.setCreated(new Date());
        sharedSolution.setComment(comment);
        List<TestResult> testResults = solution.getTestResults().stream()
                .map(TestResult::new)
                .collect(Collectors.toList());
        sharedSolution.setTestResults(testResults);

        return sharedSolutionRepository.save(sharedSolution);
    }

    @Override
    public List<SharedSolution> getSolutionsByUser(String username) {
        List<SharedSolution> sharedSolutions = sharedSolutionRepository.findAllByAuthorOrderByCreated(new User(username));
        sharedSolutions.forEach(s -> s.setChallenge(s.getChallengeDetails().getChallenge()));
        return sharedSolutions;
    }

    @Override
    public List<SharedSolution> getSolutionsByChallenge(long challengeId) {
        ChallengeDetails challengeDetails = new ChallengeDetails(challengeId);
        return sharedSolutionRepository.findAllByChallengeDetailsOrderByCreated(challengeDetails);
    }

    @Override
    public SharedSolution getSharedSolution(long sharedSolutionId) {
        SharedSolution sharedSolution = sharedSolutionRepository.findById(sharedSolutionId)
                .orElseThrow(ResourceNotFoundException::new);

        if (sharedSolution.getChallengeDetails().getShareAccess() == AccessOption.SOLVED_ONLY){
            String username = sharedSolution.getAuthor().getUsername();
            ChallengeUserPK pk = new ChallengeUserPK(username, sharedSolution.getChallengeDetails().getChallengeId());
            ChallengeUserDetails userDetails = userChallengeDetailsRepository.findById(pk).orElse(null);
            if (!challengeSolved(userDetails)){
                //Hide the implementation from users without his own solution
                sharedSolution.setImplementation(null);
            }
        }

        sharedSolution.setChallenge(sharedSolution.getChallengeDetails().getChallenge());
        return sharedSolution;
    }

    private boolean challengeSolved(ChallengeUserDetails userDetails) {
        return userDetails.getStatus() != null && userDetails.getStatus().getStatus() == ChallengeStatus.SOLVED;
    }

    @Override
    public SharedSolution updateLike(long sharedSolutionId, boolean like) {
        SharedSolution sharedSolution = sharedSolutionRepository.findById(sharedSolutionId)
                .orElseThrow(ResourceNotFoundException::new);
        this.ratingService.updateLikes(sharedSolution, like);
        return sharedSolutionRepository.save(sharedSolution);
    }
}
