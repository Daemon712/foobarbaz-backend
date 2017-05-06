package ru.foobarbaz.logic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeDetails;
import ru.foobarbaz.entity.Rating;

import java.util.List;

public interface ChallengeService {
    Challenge createChallenge(Challenge challenge);
    void updateChallengeBookmark(Long challengeId, boolean bookmark);
    Rating updateChallengeRating(Long challengeId, Rating rating);
    Challenge getChallenge(Long challengeId);
    ChallengeDetails getChallengeDetails(Long challengeId);
    List<Challenge> getChallenges();
    List<Challenge> getChallengesByAuthor(String username);
    List<Challenge> getBookmarkedChallenges(String username);
    Page<Challenge> getChallenges(Pageable page);
}
