package ru.foobarbaz.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "solutions")
public class Solution {
    @EmbeddedId
    private SolutionPK pk;

    @JoinColumns({
            @JoinColumn(name="username", referencedColumnName="username", insertable = false, updatable = false),
            @JoinColumn(name="challengeId", referencedColumnName="challengeId", insertable = false, updatable = false)
    })
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private UserChallengeDetails holder;

    @Min(SolutionStatus.EMPTY)
    @Max(SolutionStatus.ERROR)
    private int status;

    @ElementCollection
    @CollectionTable
    private List<TestResult> testResults;

    @NotNull
    private String implementation;

    public Solution() {
    }

    public Solution(SolutionPK pk) {
        this.pk = pk;
    }

    public Solution(String username, Long challengeId, Integer solutionNum) {
        this.pk = new SolutionPK(username, challengeId, solutionNum);
    }

    public SolutionPK getPk() {
        return pk;
    }

    public void setPk(SolutionPK pk) {
        this.pk = pk;
    }

    public UserChallengeDetails getHolder() {
        return holder;
    }

    public void setHolder(UserChallengeDetails holder) {
        this.holder = holder;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    @PreRemove
    private void remove() {
        getHolder().getSolutions().remove(this);
    }
}
