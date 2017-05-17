package ru.foobarbaz.entity.challenge.solution;

import com.fasterxml.jackson.annotation.JsonView;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.web.view.SolutionView;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BaseSolution {
    @NotNull
    @Enumerated
    private SolutionStatus status;

    @ElementCollection
    @CollectionTable
    @JsonView(SolutionView.Full.class)
    private List<TestResult> testResults;

    @NotNull
    @Size(max = 5000)
    @JsonView(SolutionView.Full.class)
    private String implementation;

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
}
