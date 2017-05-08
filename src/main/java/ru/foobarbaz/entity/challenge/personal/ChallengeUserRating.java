package ru.foobarbaz.entity.challenge.personal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.foobarbaz.constant.ChallengeRatingConst.*;

@Entity
public class ChallengeUserRating {
    @EmbeddedId
    @JsonIgnore
    private ChallengeUserPK pk;

    @Max(MAX_RATING)
    @Min(MIN_RATING)
    private int rating;

    @Max(MAX_DIFFICULTY)
    @Min(MIN_DIFFICULTY)
    private int difficulty;

    public ChallengeUserRating() {
    }

    public ChallengeUserRating(ChallengeUserPK pk) {
        this.pk = pk;
    }

    public ChallengeUserRating(double rating, double difficulty) {
        this.rating = (int)Math.round(rating);
        this.difficulty = (int)Math.round(difficulty);
    }

    public ChallengeUserPK getPk() {
        return pk;
    }

    public void setPk(ChallengeUserPK pk) {
        this.pk = pk;
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
