package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
public class UserChallengeDetails {

    @EmbeddedId
    @JsonIgnore
    private UserChallengePK pk;

    @Min(Challenge.MIN_RATING)
    @Max(Challenge.MAX_RATING)
    private Integer rating;

    @Min(Challenge.MIN_DIFFICULTY)
    @Max(Challenge.MAX_DIFFICULTY)
    private Integer difficulty;

    private boolean bookmark;

    public UserChallengePK getPk() {
        return pk;
    }

    public void setPk(UserChallengePK pk) {
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

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

}
