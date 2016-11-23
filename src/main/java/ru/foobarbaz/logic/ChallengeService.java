package ru.foobarbaz.logic;

import ru.foobarbaz.entity.Challenge;

import java.util.List;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    Challenge getChallenge(Long id);
    List<Challenge> getChallenges();
}
