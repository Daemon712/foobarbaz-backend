package ru.foobarbaz.logic.impl;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TestResultCollector extends RunListener {
    private LinkedHashMap<String, TestResult> testResults = new LinkedHashMap<>();

    @Override
    public void testStarted(Description description) throws Exception {
        String testName = description.getDisplayName();
        TestResult testResult = new TestResult();
        testResult.setTestName(description.getMethodName());
        testResult.setStatus(SolutionStatus.SUCCESS);
        testResults.put(testName, testResult);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        String testName = failure.getDescription().getDisplayName();
        TestResult testResult = testResults.get(testName);
        if (failure.getException() instanceof AssertionError) {
            testResult.setStatus(SolutionStatus.FAILED);
            testResult.setMessage(failure.getMessage());
        } else {
            testResult.setStatus(SolutionStatus.ERROR);
            testResult.setMessage(failure.getException().toString());
        }
    }

    public List<TestResult> getTestResults(){
        return new ArrayList<>(testResults.values());
    }
}
