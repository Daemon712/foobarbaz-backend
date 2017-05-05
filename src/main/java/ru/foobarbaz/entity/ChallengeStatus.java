package ru.foobarbaz.entity;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
public class ChallengeStatus {
    public final static int NOT_STARTED = 0;
    public final static int IN_PROGRESS = 1;
    public final static int SOLVED = 2;

    @EmbeddedId
    private UserChallengePK pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    private Challenge challenge;

    @Min(NOT_STARTED)
    @Max(SOLVED)
    private int status;

    public ChallengeStatus() {
    }

    public ChallengeStatus(UserChallengePK pk) {
        this.pk = pk;
    }

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

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
