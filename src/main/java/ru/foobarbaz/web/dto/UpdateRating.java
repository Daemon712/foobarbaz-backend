package ru.foobarbaz.web.dto;

import ru.foobarbaz.entity.Challenge;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class UpdateRating {

    @Max(Challenge.MAX_RATING)
    @Min(Challenge.MIN_RATING)
    private int rating;

    @Max(Challenge.MAX_DIFFICULTY)
    @Min(Challenge.MIN_DIFFICULTY)
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