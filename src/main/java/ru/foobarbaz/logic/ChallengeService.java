package ru.foobarbaz.logic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.repo.ChallengeRepository;

import java.util.List;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    Challenge getChallenge(Long challengeId);
    ChallengeDetails getChallengeDetails(Long challengeId);
    List<Challenge> getChallenges();
    Page<Challenge> getChallenges(Pageable pageable);
    Page<Challenge> getChallenges(Pageable pageable, ChallengeRepository.ChallengeFilter filter);
    List<Challenge> getChallengesByAuthor(String username);
    List<Challenge> getBookmarkedChallenges(String username);
    List<Challenge> quickSearchChallenges(String name);
    void updateChallengeBookmark(Long challengeId, boolean bookmark);
    void fillChallengeStatus(List<Challenge> challenges);
}
