package ru.foobarbaz.logic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.repo.ChallengeRepository;

import java.util.List;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    Challenge getChallenge(long challengeId);
    Challenge getRandomChallenge();
    ChallengeDetails getChallengeDetails(long challengeId);
    Challenge updateChallenge(Challenge challenge);
    void deleteChallenge(long challengeId);
    List<Challenge> getChallenges();
    Page<Challenge> getChallenges(Pageable pageable);
    Page<Challenge> getChallenges(Pageable pageable, ChallengeRepository.ChallengeFilter filter);
    List<Challenge> getChallengesByAuthor(String username);
    List<Challenge> getBookmarkedChallenges(String username);
    List<Challenge> quickSearchChallenges(String name);
    void updateChallengeBookmark(long challengeId, boolean bookmark);
    void fillChallengeStatus(List<Challenge> challenges);
}
