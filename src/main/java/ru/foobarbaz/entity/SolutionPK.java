package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SolutionPK implements Serializable {
    @JsonIgnore
    private String username;

    @JsonIgnore
    private Long challengeId;

    private Integer solutionNum;

    public SolutionPK() {
    }

    public SolutionPK(String username, Long challengeId, Integer solutionNum) {
        this.username = username;
        this.challengeId = challengeId;
        this.solutionNum = solutionNum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Integer getSolutionNum() {
        return solutionNum;
    }

    public void setSolutionNum(Integer solutionNum) {
        this.solutionNum = solutionNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SolutionPK)) return false;
        SolutionPK that = (SolutionPK) o;
        return Objects.equals(getUsername(), that.getUsername()) &&
                Objects.equals(getChallengeId(), that.getChallengeId()) &&
                Objects.equals(getSolutionNum(), that.getSolutionNum());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername(), getChallengeId(), getSolutionNum());
    }
}
