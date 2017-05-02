package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
public class UserChallengeDetails {

    @EmbeddedId
    @JsonIgnore
    private UserChallengePK pk;

    @ManyToOne
    @JoinColumn(name = "username", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    @JsonIgnore
    private ChallengeDetails challengeDetails;

    @OneToOne
    @JoinColumns({
            @JoinColumn(name = "username", insertable = false, updatable = false),
            @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    })
    @JsonIgnore
    private ChallengeStatus status;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ChallengeDetails getChallengeDetails() {
        return challengeDetails;
    }

    public void setChallengeDetails(ChallengeDetails challengeDetails) {
        this.challengeDetails = challengeDetails;
    }

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }

}
