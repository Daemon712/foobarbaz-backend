package ru.foobarbaz.entity;

public class TestResult {
    private String testName;
    private TestStatus status;
    private String message;

    enum TestStatus {
        SUCCESS, FAIL, ERROR
    }
}
