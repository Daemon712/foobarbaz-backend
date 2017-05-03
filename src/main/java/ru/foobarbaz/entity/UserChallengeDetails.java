package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Entity
public class UserChallengeDetails {

    @EmbeddedId
    @JsonIgnore
    private UserChallengePK pk;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "username", insertable = false, updatable = false)
    @JsonIgnore
    private UserAccount userAccount;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    @JsonIgnore
    private ChallengeDetails challengeDetails;

    @OneToOne(cascade = CascadeType.PERSIST)
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "holder")
    private List<Solution> solutions;

    public UserChallengeDetails() {
    }

    public UserChallengeDetails(UserChallengePK pk) {
        this.pk = pk;
    }

    public UserChallengePK getPk() {
        return pk;
    }

    public void setPk(UserChallengePK pk) {
        this.pk = pk;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
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

    public Integer getRating() {
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

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }
}
