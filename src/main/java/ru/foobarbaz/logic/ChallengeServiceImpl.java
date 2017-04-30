package ru.foobarbaz.logic;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeStatus;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.entity.UserChallengePK;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.repo.ChallengeStatusRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private ChallengeRepository challengeRepository;
    private ChallengeStatusRepository statusRepository;

    @Autowired
    public ChallengeServiceImpl(ChallengeRepository challengeRepository, ChallengeStatusRepository statusRepository) {
        this.challengeRepository = challengeRepository;
        this.statusRepository = statusRepository;
    }

    @Override
    public Challenge createChallenge(Challenge template) {
        Challenge challenge = new Challenge();
        BeanUtils.copyProperties(template, challenge);
        challenge.setCreated(new Date());
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        challenge.setAuthor(new User(author));
        Challenge createdChallenge = challengeRepository.save(challenge);

        ChallengeStatus challengeStatus = new ChallengeStatus();
        challengeStatus.setPk(new UserChallengePK(author, createdChallenge.getId()));
        challengeStatus.setStatus(ChallengeStatus.SOLVED);
        statusRepository.save(challengeStatus);
        return challenge;
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
                .peek(c -> c.setStatus(statusMap.getOrDefault(c.getId(), ChallengeStatus.NOT_STARTED)))
                .collect(Collectors.toList());
    }
}
