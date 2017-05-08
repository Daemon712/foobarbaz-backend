package ru.foobarbaz.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.AccessOption;
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.entity.challenge.Challenge;
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
    private UserAccountRepository userAccountRepository;

    @Autowired
    public SharedSolutionServiceImpl(UserChallengeDetailsRepository userChallengeDetailsRepository,
                                     SolutionRepository solutionRepository,
                                     SharedSolutionRepository sharedSolutionRepository,
                                     UserAccountRepository userAccountRepository) {
        this.userChallengeDetailsRepository = userChallengeDetailsRepository;
        this.solutionRepository = solutionRepository;
        this.sharedSolutionRepository = sharedSolutionRepository;
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

        SharedSolution sharedSolution = new SharedSolution();
        sharedSolution.setChallengeDetails(new ChallengeDetails(solutionPK.getChallengeId()));
        sharedSolution.setAuthor(new User(solutionPK.getUsername()));
        sharedSolution.setStatus(solution.getStatus());
        sharedSolution.setImplementation(solution.getImplementation());
        sharedSolution.setCreated(new Date());
        sharedSolution.setComment(comment);
        List<TestResult> testResults = solution.getTestResults().stream()
                .map(TestResult::new)
                .collect(Collectors.toList());
        sharedSolution.setTestResults(testResults);

        UserAccount userAccount = solution.getHolder().getUserAccount();
        userAccount.setSharedSolutions(userAccount.getSharedSolutions() + 1);
        userAccountRepository.save(userAccount);

        return sharedSolutionRepository.save(sharedSolution);
    }

    @Override
    public List<SharedSolution> getSolutionsByUser(String username) {
        List<SharedSolution> sharedSolutions = sharedSolutionRepository.findAllByAuthorOrderByCreated(new User(username));
        sharedSolutions.forEach(this::clearDetails);
        sharedSolutions.forEach(sharedSolution -> {
            Challenge source = sharedSolution.getChallengeDetails().getChallenge();
            Challenge target = new Challenge();
            target.setChallengeId(source.getChallengeId());
            target.setName(source.getName());
            target.setShortDescription(source.getShortDescription());
            sharedSolution.setChallenge(target);
            sharedSolution.setChallengeDetails(null);
            sharedSolution.setAuthor(null);
        });
        return sharedSolutions;
    }

    @Override
    public List<SharedSolution> getSolutionsByChallenge(long challengeId) {
        ChallengeDetails challengeDetails = new ChallengeDetails(challengeId);
        List<SharedSolution> sharedSolutions = sharedSolutionRepository.findAllByChallengeDetailsOrderByCreated(challengeDetails);
        sharedSolutions.forEach(this::clearDetails);
        sharedSolutions.forEach(sharedSolution -> sharedSolution.setChallengeDetails(null));
        return sharedSolutions;
    }

    private void clearDetails(SharedSolution sharedSolution){
        sharedSolution.setTestResults(null);
        sharedSolution.setImplementation(null);
    }

    @Override
    public SharedSolution getSharedSolution(long sharedSolutionId) {
        SharedSolution sharedSolution = sharedSolutionRepository.findById(sharedSolutionId)
                .orElseThrow(ResourceNotFoundException::new);

        if (sharedSolution.getChallengeDetails().getShareAccess() == AccessOption.SOLVED_ONLY){
            String username = sharedSolution.getAuthor().getUsername();
            ChallengeUserPK pk = new ChallengeUserPK(username, sharedSolution.getChallengeDetails().getChallengeId());
            ChallengeUserDetails userDetails = userChallengeDetailsRepository.findById(pk).orElse(null);
            if (challengeSolved(userDetails)){
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
    public void updateLike(long sharedSolutionId, boolean like) {

    }
}
