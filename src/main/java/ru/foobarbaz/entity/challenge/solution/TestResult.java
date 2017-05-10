package ru.foobarbaz.entity.challenge.solution;

import ru.foobarbaz.constant.SolutionStatus;

import javax.persistence.Embeddable;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
@Embeddable
public class TestResult implements Serializable {
    @NotNull
    private String testName;

    @NotNull
    @Enumerated
    private SolutionStatus status;

    @Size(max = 250)
    private String message;

    public TestResult() {
    }

    public TestResult(String testName, SolutionStatus status) {
        this.testName = testName;
        this.status = status;
    }

    public TestResult(String testName, SolutionStatus status, String message) {
        this.testName = testName;
        this.status = status;
        this.message = message;
    }

    public TestResult(TestResult original) {
        this.testName = original.getTestName();
        this.status = original.getStatus();
        this.message = original.getMessage();
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestResult)) return false;
        TestResult that = (TestResult) o;
        return Objects.equals(getTestName(), that.getTestName()) &&
                getStatus() == that.getStatus() &&
                Objects.equals(getMessage(), that.getMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTestName(), getStatus(), getMessage());
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "testName='" + testName + '\'' +
                ", status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
