package ru.foobarbaz.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class ChallengeStatus {
    public final static int NOT_STARTED = 0;
    public final static int IN_PROGRESS = 1;
    public final static int SOLVED = 2;

    @EmbeddedId
    private UserChallengePK pk;

    private int status;

    public UserChallengePK getPk() {
        return pk;
    }

    public void setPk(UserChallengePK pk) {
        this.pk = pk;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
