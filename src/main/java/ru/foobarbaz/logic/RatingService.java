package ru.foobarbaz.logic;

import ru.foobarbaz.entity.challenge.personal.ChallengeUserRating;

public interface RatingService {
    ChallengeUserRating updateChallengeRating(ChallengeUserRating rating);
}
