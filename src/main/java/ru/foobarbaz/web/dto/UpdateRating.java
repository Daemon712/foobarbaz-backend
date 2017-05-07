package ru.foobarbaz.web.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.foobarbaz.constant.ChallengeRatingConst.*;

public class UpdateRating {

    @Max(MAX_RATING)
    @Min(MIN_RATING)
    private int rating;

    @Max(MAX_DIFFICULTY)
    @Min(MIN_DIFFICULTY)
    private int difficulty;

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}