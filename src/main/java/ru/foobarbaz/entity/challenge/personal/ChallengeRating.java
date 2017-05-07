package ru.foobarbaz.entity.challenge.personal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.foobarbaz.entity.challenge.Challenge;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.foobarbaz.constant.ChallengeRatingConst.*;

@Entity
public class ChallengeRating {
    @EmbeddedId
    @JsonIgnore
    private UserChallengePK pk;

    @ManyToOne
    @JsonIgnore
    private Challenge challenge;

    @Max(MAX_RATING)
    @Min(MIN_RATING)
    private int rating;

    @Max(MAX_DIFFICULTY)
    @Min(MIN_DIFFICULTY)
    private int difficulty;

    public ChallengeRating() {
    }

    public ChallengeRating(UserChallengePK pk) {
        this.pk = pk;
    }

    public ChallengeRating(double rating, double difficulty) {
        this.rating = (int)Math.round(rating);
        this.difficulty = (int)Math.round(difficulty);
    }

    public UserChallengePK getPk() {
        return pk;
    }

    public void setPk(UserChallengePK pk) {
        this.pk = pk;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
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
