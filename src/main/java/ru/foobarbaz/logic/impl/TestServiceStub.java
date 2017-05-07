package ru.foobarbaz.logic.impl;

import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.logic.TestService;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ru.foobarbaz.constant.SolutionStatus.*;

@Service
public class TestServiceStub implements TestService {
    private String[] testNames = {
            "testOne", "testTwo", "testFive",
            "testTen", "testZero", "testNegative",
    };
    @Override
    public List<TestResult> executeTests(String unitTest, String code) {
        Random random = new Random(unitTest.hashCode() + code.hashCode());
        return Arrays.stream(testNames).map(testName -> {
            TestResult result = new TestResult();
            result.setTestName(testName);

            double r = random.nextDouble();
            SolutionStatus status = r < 0.8 ? SUCCESS : r < 0.95 ? FAILED : ERROR;
            result.setStatus(status);

            if (status == FAILED) result.setMessage("Expected: 1, Actual: 0");
            if (status == ERROR) result.setMessage("NullPointerException");
            return result;
        }).collect(Collectors.toList());
    }
}
