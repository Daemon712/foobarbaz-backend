package ru.foobarbaz.logic;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.*;
import ru.foobarbaz.repo.ChallengeDetailsRepository;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.repo.ChallengeStatusRepository;
import ru.foobarbaz.repo.UserChallengeDetailsRepository;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private ChallengeRepository challengeRepository;
    private ChallengeStatusRepository statusRepository;
    private ChallengeDetailsRepository detailsRepository;
    private UserChallengeDetailsRepository userDetailsRepository;

    @Autowired
    public ChallengeServiceImpl(
            ChallengeRepository challengeRepository,
            ChallengeStatusRepository statusRepository,
            ChallengeDetailsRepository detailsRepository,
            UserChallengeDetailsRepository userDetailsRepository) {
        this.challengeRepository = challengeRepository;
        this.statusRepository = statusRepository;
        this.detailsRepository = detailsRepository;
        this.userDetailsRepository = userDetailsRepository;
    }

    @Override
    public Challenge createChallenge(Challenge template) {
        String author = SecurityContextHolder.getContext().getAuthentication().getName();

        Challenge challenge = new Challenge();
        BeanUtils.copyProperties(template, challenge);
        challenge.setRating(Challenge.MAX_RATING);
        challenge.setCreated(new Date());
        challenge.setAuthor(new User(author));
        challenge.setDetails(null);
        Challenge savedChallenge = challengeRepository.save(challenge);

        ChallengeDetails details = new ChallengeDetails();
        BeanUtils.copyProperties(template.getDetails(), details);
        details.setSolutions(1);//At least the author has solved it
        details.setChallenge(savedChallenge);
        ChallengeDetails savedDetails = detailsRepository.save(details);
        savedChallenge.setDetails(savedDetails);

        UserChallengeDetails userDetails = new UserChallengeDetails();
        userDetails.setPk(new UserChallengePK(author, savedChallenge.getId()));
        userDetails.setRating(savedChallenge.getRating());
        userDetails.setDifficulty(savedChallenge.getDifficulty());
        userDetailsRepository.save(userDetails);

        ChallengeStatus challengeStatus = new ChallengeStatus();
        challengeStatus.setPk(new UserChallengePK(author, savedChallenge.getId()));
        challengeStatus.setStatus(ChallengeStatus.SOLVED);
        statusRepository.save(challengeStatus);

        return savedChallenge;
    }

    @Override
    public Challenge getChallenge(Long id) {
        Challenge challenge = challengeRepository.findOne(id).orElseThrow(ResourceNotFoundException::new);
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        ChallengeStatus status = user != null ? statusRepository.findOne(new UserChallengePK(user, id)).orElse(null) : null;
        challenge.setStatus(status != null ? status.getStatus() : ChallengeStatus.NOT_STARTED);
        return challenge;
    }

    @Override
    public ChallengeDetails getChallengeDetails(Long id) {
        ChallengeDetails details = detailsRepository.findOne(id).orElseThrow(ResourceNotFoundException::new);
        details.setViews(details.getViews() + 1);
        detailsRepository.save(details);

        String user = SecurityContextHolder.getContext().getAuthentication().getName();

        ChallengeStatus status = user != null ? statusRepository.findOne(new UserChallengePK(user, id)).orElse(null) : null;
        details.getChallenge().setStatus(status != null ? status.getStatus() : ChallengeStatus.NOT_STARTED);

        UserChallengeDetails userDetails = userDetailsRepository.findOne(new UserChallengePK(user, id)).orElse(null);
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
                .peek(c -> c.setStatus(statusMap.getOrDefault(c.getId(), ChallengeStatus.NOT_STARTED)))
                .collect(Collectors.toList());
    }
}
