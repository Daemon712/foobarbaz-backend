package ru.foobarbaz.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.User;
import ru.foobarbaz.repo.ChallengeRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ChallengeServiceImpl implements ChallengeService {
    private ChallengeRepository challengeRepository;

    @Autowired
    public ChallengeServiceImpl(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    @Override
    public Challenge createChallenge(Challenge template) {
        Challenge challenge = new Challenge();
        challenge.setName(template.getName());
        challenge.setUnitTest(template.getUnitTest());
        challenge.setTemplate(template.getTemplate());
        challenge.setCreated(new Date());
        String author = SecurityContextHolder.getContext().getAuthentication().getName();
        challenge.setAuthor(new User(author));
        challenge.setDescription(template.getDescription());
        challengeRepository.save(challenge);
        return challenge;
    }

    @Override
    public Challenge getChallenge(Long id) {
        Challenge challenge = challengeRepository.findOne(id);
        challenge.setStatus(Math.random() < 0.5);//TODO
        return challenge;
    }

    @Override
    public List<Challenge> getChallenges() {
        return StreamSupport
                .stream(challengeRepository.findAll().spliterator(), true)
                .peek(challenge -> challenge.setStatus(Math.random() < 0.5))//TODO:
                .collect(Collectors.toList());
    }
}