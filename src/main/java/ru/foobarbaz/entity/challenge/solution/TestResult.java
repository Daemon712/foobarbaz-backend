package ru.foobarbaz.entity.challenge.solution;

import ru.foobarbaz.constant.SolutionStatus;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Embeddable
public class TestResult implements Serializable {
    @NotNull
    private String testName;

    @NotNull
    @Enumerated
    private SolutionStatus status;

    @Size(max = 250)
    private String message;

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public SolutionStatus getStatus() {
        return status;
    }

    public void setStatus(SolutionStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
