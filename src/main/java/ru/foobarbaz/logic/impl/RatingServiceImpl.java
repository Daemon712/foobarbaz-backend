package ru.foobarbaz.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.AbleToLikes;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;
import ru.foobarbaz.entity.user.User;
import ru.foobarbaz.entity.user.UserAccount;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserPK;
import ru.foobarbaz.exception.DeniedOperationException;
import ru.foobarbaz.logic.RatingService;
import ru.foobarbaz.repo.ChallengeRepository;
import ru.foobarbaz.repo.UserAccountRepository;
import ru.foobarbaz.repo.UserChallengeDetailsRepository;
import ru.foobarbaz.repo.UserChallengeRatingRepository;

import javax.transaction.Transactional;

@Service
public class RatingServiceImpl implements RatingService {
    private ChallengeRepository challengeRepository;
    private UserChallengeRatingRepository userRatingRepository;
    private UserChallengeDetailsRepository userDetailsRepository;
    private UserAccountRepository userAccountRepository;

    @Autowired
    public RatingServiceImpl(
            ChallengeRepository challengeRepository,
            UserChallengeRatingRepository userRatingRepository,
            UserChallengeDetailsRepository userDetailsRepository,
            UserAccountRepository userAccountRepository) {
        this.challengeRepository = challengeRepository;
        this.userRatingRepository = userRatingRepository;
        this.userDetailsRepository = userDetailsRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public ChallengeUserRating updateChallengeRating(ChallengeUserRating newRating) {
        ChallengeUserPK pk = newRating.getPk();
        ChallengeUserDetails userDetails = userDetailsRepository.findById(pk).orElse(new ChallengeUserDetails(pk));
        ChallengeUserRating oldRating = userDetails.getRating();
        userDetails.setRating(newRating);
        userDetailsRepository.save(userDetails);

        Challenge challenge = challengeRepository.findById(pk.getChallengeId())
                .orElseThrow(ResourceNotFoundException::new);

        if (!pk.getUsername().equals(challenge.getAuthor().getUsername()))
            updateUserAccountRating(challenge.getAuthor().getUsername(), oldRating, newRating);

        ChallengeUserRating avgRating = userRatingRepository.calcAvgRating(challenge.getChallengeId());
        challenge.setRating(avgRating.getRating());
        challenge.setDifficulty(avgRating.getDifficulty());
        challengeRepository.save(challenge);
        return avgRating;
    }

    @Override
    public void updateLikes(AbleToLikes entity, boolean like) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username.equals(entity.getAuthor().getUsername()))
            throw new DeniedOperationException("author can't like his own object");

        UserAccount author = entity.getAuthor().getAccount();

        if (like) {
            if (entity.isLiked()) throw new DeniedOperationException("user can't like object twice");
            entity.getLikes().add(new User(username));
            author.setRating(author.getRating() + 1);
        } else {
            if (!entity.isLiked()) throw new DeniedOperationException("user can't unlike not liked object");
            entity.getLikes().remove(new User(username));
            author.setRating(author.getRating() - 1);
        }

        userAccountRepository.save(author);
    }

    private void updateUserAccountRating(String username, ChallengeUserRating oldRating, ChallengeUserRating newRating) {
        UserAccount account = userAccountRepository.findById(username).orElseThrow(ResourceNotFoundException::new);
        int calculatedRating  = account.getRating() + convertRating(newRating);
        if (oldRating != null) calculatedRating -= convertRating(oldRating);
        account.setRating(calculatedRating);
        userAccountRepository.save(account);
    }

    private static int convertRating(ChallengeUserRating challengeRating){
        int r = challengeRating.getRating();
        return (int) Math.floor(r * r / 3);
    }
}
