package ru.foobarbaz.entity.challenge.personal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.foobarbaz.entity.challenge.ChallengeDetails;
import ru.foobarbaz.entity.challenge.solution.Solution;
import ru.foobarbaz.entity.user.UserAccount;

import javax.persistence.*;
import java.util.List;

@Entity
public class ChallengeUserDetails {

    @EmbeddedId
    @JsonIgnore
    private ChallengeUserPK pk;

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
    private ChallengeUserStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "username", insertable = false, updatable = false),
            @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    })
    private ChallengeUserRating rating;

    private boolean bookmark;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "holder")
    private List<Solution> solutions;

    public ChallengeUserDetails() {
    }

    public ChallengeUserDetails(ChallengeUserPK pk) {
        this.pk = pk;
    }

    public ChallengeUserPK getPk() {
        return pk;
    }

    public void setPk(ChallengeUserPK pk) {
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

    public ChallengeUserStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeUserStatus status) {
        this.status = status;
    }

    public ChallengeUserRating getRating() {
        return rating;
    }

    public void setRating(ChallengeUserRating rating) {
        this.rating = rating;
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
