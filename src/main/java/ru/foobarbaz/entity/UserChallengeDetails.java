package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "username", insertable = false, updatable = false),
            @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    })
    @JsonIgnore
    private ChallengeStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "username", insertable = false, updatable = false),
            @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    })
    private ChallengeRating userRating;

    private boolean bookmark;

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

    public ChallengeRating getUserRating() {
        return userRating;
    }

    public void setUserRating(ChallengeRating userRating) {
        this.userRating = userRating;
    }

    public List<Solution> getSolutions() {
        return solutions;
    }

    public void setSolutions(List<Solution> solutions) {
        this.solutions = solutions;
    }

    public boolean isBookmark() {
        return bookmark;
    }

    public void setBookmark(boolean bookmark) {
        this.bookmark = bookmark;
    }
}
