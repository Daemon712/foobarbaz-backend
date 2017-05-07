package ru.foobarbaz.entity.challenge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ru.foobarbaz.constant.AccessOption;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class ChallengeDetails {
    @Id
    @JsonIgnore
    private Long challengeId;

    @MapsId
    @OneToOne
    @JoinColumn(name="challengeId")
    @JsonIgnore
    private Challenge challenge;

    @NotNull
    @Size(min = 100, max = 5000)
    private String fullDescription;

    @NotNull
    @Size(max = 5000)
    private String template;

    @NotNull
    @Size(max = 10000)
    @JsonIgnore
    private String unitTest;

    @Transient
    @JsonIgnore
    private String sample;

    private int views;

    private int solutions;

    @Transient
    private ChallengeUserDetails userDetails;

    @Enumerated
    private AccessOption commentAccess;

    @Enumerated
    private AccessOption shareAccess;

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getUnitTest() {
        return unitTest;
    }

    public void setUnitTest(String unitTest) {
        this.unitTest = unitTest;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getSolutions() {
        return solutions;
    }

    public void setSolutions(int solutions) {
        this.solutions = solutions;
    }

    public ChallengeUserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(ChallengeUserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public AccessOption getCommentAccess() {
        return commentAccess;
    }

    public void setCommentAccess(AccessOption commentAccess) {
        this.commentAccess = commentAccess;
    }

    public AccessOption getShareAccess() {
        return shareAccess;
    }

    public void setShareAccess(AccessOption shareAccess) {
        this.shareAccess = shareAccess;
    }
}
