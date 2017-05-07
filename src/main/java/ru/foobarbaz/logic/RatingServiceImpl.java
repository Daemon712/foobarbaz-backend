package ru.foobarbaz.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.Challenge;
import ru.foobarbaz.entity.ChallengeRating;
import ru.foobarbaz.entity.UserAccount;
import ru.foobarbaz.entity.UserChallengePK;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserChallengeRatingRepository;

import javax.transaction.Transactional;

@Service
public class RatingServiceImpl implements RatingService {
    private ChallengeRepository challengeRepository;
    private UserChallengeRatingRepository userChallengeRatingRepository;
    private UserAccountRepository userAccountRepository;

    @Autowired
    public RatingServiceImpl(
            ChallengeRepository challengeRepository,
            UserChallengeRatingRepository userChallengeRatingRepository,
            UserAccountRepository userAccountRepository) {
        this.challengeRepository = challengeRepository;
        this.userChallengeRatingRepository = userChallengeRatingRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public ChallengeRating updateChallengeRating(ChallengeRating rating) {
        UserChallengePK pk = rating.getPk();
        ChallengeRating oldRating = userChallengeRatingRepository.findById(pk).orElse(null);
        ChallengeRating newRating = userChallengeRatingRepository.save(rating);

        Challenge challenge = oldRating != null
                ? oldRating.getChallenge()
                : challengeRepository.findById(pk.getChallengeId())
                .orElseThrow(ResourceNotFoundException::new);

        if (!pk.getUsername().equals(challenge.getAuthor().getUsername()))
            updateUserAccountRating(challenge.getAuthor().getUsername(), oldRating, newRating);

        ChallengeRating avgRating = userChallengeRatingRepository.calcAvgRating(challenge.getChallengeId());
        challenge.setRating(avgRating.getRating());
        challenge.setDifficulty(avgRating.getDifficulty());
        challengeRepository.save(challenge);
        return avgRating;
    }

    private void updateUserAccountRating(String username, ChallengeRating oldRating, ChallengeRating newRating) {
        UserAccount account = userAccountRepository.findById(username).orElseThrow(ResourceNotFoundException::new);
        int calculatedRating  = account.getRating() + convertRating(newRating);
        if (oldRating != null) calculatedRating -= convertRating(oldRating);
        account.setRating(calculatedRating);
        userAccountRepository.save(account);
    }

    private static int convertRating(ChallengeRating challengeRating){
        int r = challengeRating.getRating();
        return (int) Math.floor(r * r / 3);
    }
}
