package ru.foobarbaz.entity.challenge.solution;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.personal.ChallengeUserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
public class Solution {
    @EmbeddedId
    @JsonProperty("solutionNum")
    private SolutionPK pk;

    @JoinColumns({
            @JoinColumn(name="username", referencedColumnName="username", insertable = false, updatable = false),
            @JoinColumn(name="challengeId", referencedColumnName="challengeId", insertable = false, updatable = false)
    })
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonIgnore
    private ChallengeUserDetails holder;

    @NotNull
    @Enumerated
    private SolutionStatus status;

    @ElementCollection
    @CollectionTable
    private List<TestResult> testResults;

    @NotNull
    @Size(max = 5000)
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

    public ChallengeUserDetails getHolder() {
        return holder;
    }

    public void setHolder(ChallengeUserDetails holder) {
        this.holder = holder;
    }

    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public SolutionStatus getStatus() {
        return status;
    }

    public void setStatus(SolutionStatus status) {
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
