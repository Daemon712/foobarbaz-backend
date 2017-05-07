package ru.foobarbaz.logic;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.*;
import ru.foobarbaz.repo.ChallengeDetailsRepository;
import ru.foobarbaz.repo.SolutionRepository;
import ru.foobarbaz.repo.UserChallengeDetailsRepository;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

@Service
public class SolutionServiceImpl implements SolutionService {
    private SolutionRepository solutionRepository;
    private UserChallengeDetailsRepository userChallengeDetailsRepository;
    private ChallengeDetailsRepository challengeDetailsRepository;
    private TestService testService;
    private final static int MAX_SOLUTIONS = 10;

    @Autowired
    public SolutionServiceImpl(
            SolutionRepository solutionRepository,
            UserChallengeDetailsRepository userChallengeDetailsRepository,
            ChallengeDetailsRepository challengeDetailsRepository,
            TestService testService) {
        this.solutionRepository = solutionRepository;
        this.userChallengeDetailsRepository = userChallengeDetailsRepository;
        this.challengeDetailsRepository = challengeDetailsRepository;
        this.testService = testService;
    }

    @Override
    public Solution saveSolution(Solution template) {
        Solution solution = prepareAndValidate(template);
        solution.setTestResults(Collections.emptyList());
        solution.setStatus(SolutionStatus.EMPTY);
        return solutionRepository.save(solution);
    }

    @Override
    public Solution testSolution(Solution template) {
        Solution solution = prepareAndValidate(template);

        if (solution.getHolder().getChallengeDetails() == null){
            ChallengeDetails challengeDetails = challengeDetailsRepository.findById(solution.getPk().getChallengeId())
                    .orElseThrow(ResourceNotFoundException::new);
            solution.getHolder().setChallengeDetails(challengeDetails);
        }
        String unitTest = solution.getHolder().getChallengeDetails().getUnitTest();
        String impl = solution.getImplementation();
        List<TestResult> results = testService.executeTests(unitTest, impl);
        solution.setTestResults(results);

        int status = results.stream().mapToInt(TestResult::getStatus).max().orElse(SolutionStatus.SUCCESS);
        solution.setStatus(status);

        if (status == SolutionStatus.SUCCESS){
            handleSolutionSolved(solution);
        }
        return solutionRepository.save(solution);
    }

    private void handleSolutionSolved(Solution solution) {
        ChallengeStatus status = solution.getHolder().getStatus();
        if (status != null && status.getStatus() == ChallengeStatus.SOLVED) return;

        if (status == null) status = new ChallengeStatus();
        status.setStatus(ChallengeStatus.SOLVED);

        ChallengeDetails details = solution.getHolder().getChallengeDetails();
        details.setSolutions(details.getSolutions() + 1);

        UserAccount userAccount = solution.getHolder().getUserAccount();
        userAccount.setSolutions(userAccount.getSolutions() + 1);
    }

    private Solution prepareAndValidate(Solution template) {
        SolutionPK pk = new SolutionPK();
        BeanUtils.copyProperties(template.getPk(), pk);
        Solution solution = solutionRepository.findById(pk).orElse(new Solution(pk));
        solution.setImplementation(template.getImplementation());

        UserChallengePK userChallengePK = new UserChallengePK(pk.getUsername(), pk.getChallengeId());
        UserChallengeDetails holder = solution.getHolder() != null ?
                solution.getHolder() :
                userChallengeDetailsRepository.findById(userChallengePK)
                        .orElse(new UserChallengeDetails(userChallengePK));
        solution.setHolder(holder);

        ChallengeStatus challengeStatus = holder.getStatus() != null ?
                holder.getStatus() :
                new ChallengeStatus(userChallengePK);

        if (challengeStatus.getStatus() == ChallengeStatus.NOT_STARTED){
            challengeStatus.setStatus(ChallengeStatus.IN_PROGRESS);
        }
        holder.setStatus(challengeStatus);

        if (holder.getSolutions() == null){
            pk.setSolutionNum(1);
        } else if (!exists(holder.getSolutions(), solution.getPk().getSolutionNum())){
            if (holder.getSolutions().size() >= MAX_SOLUTIONS){
                throw new IllegalStateException(MessageFormat.format("Only {0} solution for one user and one challenge are allowed", MAX_SOLUTIONS));
            }
            int solNum = generateSolutionNum(holder.getSolutions());
            pk.setSolutionNum(solNum);
        }
        return solution;
    }

    private boolean exists(List<Solution> solutions, Integer solutionNum){
        return solutionNum != null &&
                solutions.stream()
                        .map(Solution::getPk)
                        .map(SolutionPK::getSolutionNum)
                        .anyMatch(solutionNum::equals);
    }

    private int generateSolutionNum(List<Solution> solutions){
        return 1 + solutions.stream()
                .map(Solution::getPk)
                .mapToInt(SolutionPK::getSolutionNum)
                .max()
                .orElse(0);
    }
}
