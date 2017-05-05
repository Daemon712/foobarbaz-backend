package ru.foobarbaz.logic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeDetails;

import java.util.List;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    Challenge getChallenge(Long challengeId);
    ChallengeDetails getChallengeDetails(Long challengeId);
    List<Challenge> getChallenges();
    Page<Challenge> getChallenges(Pageable page);
}
