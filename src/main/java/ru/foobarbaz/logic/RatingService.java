package ru.foobarbaz.logic;

import ru.foobarbaz.entity.AbleToLikes;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;

public interface RatingService {
    ChallengeUserRating updateChallengeRating(ChallengeUserRating rating);
    void updateLikes(AbleToLikes entity, boolean like);
}
