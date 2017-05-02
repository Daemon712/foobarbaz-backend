package ru.foobarbaz.logic;

import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeDetails;

import java.util.List;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    Challenge getChallenge(Long challengeId);
    ChallengeDetails getChallengeDetails(Long challengeId);
    List<Challenge> getChallenges();
}
