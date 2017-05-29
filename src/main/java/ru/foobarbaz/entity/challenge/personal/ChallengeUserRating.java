package ru.foobarbaz.entity.challenge.personal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static ru.foobarbaz.constant.ChallengeRatingConst.*;

@Entity
public class ChallengeUserRating {
    @EmbeddedId
    @JsonIgnore
    private ChallengeUserPK pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    @JsonIgnore
    private Challenge challenge;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
