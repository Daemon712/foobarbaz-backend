package ru.foobarbaz.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class ChallengeDetails {
    @Id
    @JsonIgnore
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Challenge challenge;

    @NotNull
    @Size(min = 100, max = 2000)
    private String fullDescription;

    @NotNull
    private String template;

    @NotNull
    @JsonIgnore
    private String unitTest;

    private int views;

    private int solutions;

    @Transient
    private UserChallengeDetails userDetails;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public UserChallengeDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserChallengeDetails userDetails) {
        this.userDetails = userDetails;
    }
}
