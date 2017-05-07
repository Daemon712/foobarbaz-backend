package ru.foobarbaz.logic.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserStatus;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.entity.challenge.solution.Solution;
import ru.foobarbaz.entity.challenge.solution.SolutionPK;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.logic.SolutionService;
import ru.foobarbaz.logic.TestService;
import ru.foobarbaz.repo.ChallengeDetailsRepository;
import ru.foobarbaz.repo.SolutionRepository;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserChallengeDetailsRepository;

import javax.transaction.Transactional;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SolutionServiceImpl implements SolutionService {
    private SolutionRepository solutionRepository;
    private UserAccountRepository accountRepository;
    private UserChallengeDetailsRepository userChallengeDetailsRepository;
    private ChallengeDetailsRepository challengeDetailsRepository;
    private TestService testService;
    private final static int MAX_SOLUTIONS = 10;

    @Autowired
    public SolutionServiceImpl(
            SolutionRepository solutionRepository,
            UserAccountRepository accountRepository,
            UserChallengeDetailsRepository userChallengeDetailsRepository,
            ChallengeDetailsRepository challengeDetailsRepository,
            TestService testService) {
        this.solutionRepository = solutionRepository;
        this.accountRepository = accountRepository;
        this.userChallengeDetailsRepository = userChallengeDetailsRepository;
        this.challengeDetailsRepository = challengeDetailsRepository;
        this.testService = testService;
    }

    @Override
    @Transactional
    public Solution saveSolution(Solution template) {
        Solution solution = prepareAndValidate(template);
        solution.setTestResults(Collections.emptyList());
        solution.setStatus(SolutionStatus.EMPTY);
        return solutionRepository.save(solution);
    }

    @Override
    @Transactional
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

        SolutionStatus status = results.stream()
                .map(TestResult::getStatus)
                .max(Comparator.comparing(Enum::ordinal))
                .orElse(SolutionStatus.SUCCESS);
        solution.setStatus(status);

        if (status == SolutionStatus.SUCCESS){
            handleSolutionSolved(solution);
        }
        return solutionRepository.save(solution);
    }

    private void handleSolutionSolved(Solution solution) {
        ChallengeUserStatus status = solution.getHolder().getStatus();
        if (status != null && status.getStatus() == ChallengeStatus.SOLVED) return;

        if (status == null) status = new ChallengeUserStatus();
        status.setStatus(ChallengeStatus.SOLVED);

        ChallengeDetails details = solution.getHolder().getChallengeDetails();
        details.setSolutions(details.getSolutions() + 1);

        UserAccount userAccount = solution.getHolder().getUserAccount();
        if (userAccount == null) {
            userAccount = accountRepository.findById(solution.getPk().getUsername())
                    .orElseThrow(ResourceNotFoundException::new);
            solution.getHolder().setUserAccount(userAccount);
        }
        userAccount.setSolutions(userAccount.getSolutions() + 1);
    }

    private Solution prepareAndValidate(Solution template) {
        SolutionPK pk = new SolutionPK();
        BeanUtils.copyProperties(template.getPk(), pk);
        Solution solution = solutionRepository.findById(pk).orElse(new Solution(pk));
        solution.setImplementation(template.getImplementation());

        ChallengeUserPK userChallengePK = new ChallengeUserPK(pk.getUsername(), pk.getChallengeId());
        ChallengeUserDetails holder = solution.getHolder() != null ?
                solution.getHolder() :
                userChallengeDetailsRepository.findById(userChallengePK)
                        .orElse(new ChallengeUserDetails(userChallengePK));
        solution.setHolder(holder);

        if (holder.getStatus() == null) holder.setStatus(new ChallengeUserStatus(userChallengePK));
        if (holder.getStatus().getStatus() != ChallengeStatus.SOLVED){
            holder.getStatus().setStatus(ChallengeStatus.IN_PROGRESS);
        }

        if (holder.getSolutions() == null){
            pk.setSolutionNum(1);
        } else if (!exists(holder.getSolutions(), solution.getPk().getSolutionNum())){
            if (holder.getSolutions().size() >= MAX_SOLUTIONS){
                throw new IllegalStateException(MessageFormat.format("Only {0} solution for one personal and one challenge are allowed", MAX_SOLUTIONS));
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
