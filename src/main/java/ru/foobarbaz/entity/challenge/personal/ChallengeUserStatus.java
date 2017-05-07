package ru.foobarbaz.entity.challenge.personal;

import ru.foobarbaz.constant.ChallengeStatus;
import ru.foobarbaz.entity.challenge.Challenge;
import ru.foobarbaz.entity.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class ChallengeUserStatus {
    @EmbeddedId
    private ChallengeUserPK pk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challengeId", insertable = false, updatable = false)
    private Challenge challenge;

    @NotNull
    @Enumerated
    private ChallengeStatus status;

    public ChallengeUserStatus() {
    }

    public ChallengeUserStatus(ChallengeUserPK pk) {
        this.pk = pk;
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

    public ChallengeStatus getStatus() {
        return status;
    }

    public void setStatus(ChallengeStatus status) {
        this.status = status;
    }
}
