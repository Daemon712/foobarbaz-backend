package ru.foobarbaz.entity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class Rating {
    @Max(Challenge.MAX_RATING)
    @Min(Challenge.MIN_RATING)
    private int rating;

    @Max(Challenge.MAX_DIFFICULTY)
    @Min(Challenge.MIN_DIFFICULTY)
    private int difficulty;

    public Rating() {
    }

    public Rating(int rating, int difficulty) {
        this.rating = rating;
        this.difficulty = difficulty;
    }

    public Rating(double rating, double difficulty) {
        this((int)Math.round(rating), (int)Math.round(difficulty));
    }

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
