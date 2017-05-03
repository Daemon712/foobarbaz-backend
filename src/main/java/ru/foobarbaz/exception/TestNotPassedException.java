package ru.foobarbaz.exception;

import ru.foobarbaz.entity.TestResult;

import java.util.List;

public class TestNotPassedException extends RuntimeException {
    private List<TestResult> testResultList;

    public TestNotPassedException(List<TestResult> testResultList) {
        this.testResultList = testResultList;
    }

    public List<TestResult> getTestResultList() {
        return testResultList;
    }
}
