package ru.foobarbaz.entity.challenge.personal;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ChallengeUserPK implements Serializable {
    private String username;

    private long challengeId;

    public ChallengeUserPK() {
    }

    public ChallengeUserPK(String username, long challengeId) {
        this.username = username;
        this.challengeId = challengeId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChallengeUserPK)) return false;
        ChallengeUserPK that = (ChallengeUserPK) o;
        return getChallengeId() == that.getChallengeId() &&
                Objects.equals(getUsername(), that.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getChallengeId());
    }
}
