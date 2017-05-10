package ru.foobarbaz.logic.impl.test;

import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;

import java.util.function.Function;

@Service
public class TestRunResultConverter implements Function<ResultItem, TestResult>{
    private static final String ASSERT_CLASS_NAME = AssertionError.class.getName();

    @Override
    public TestResult apply(ResultItem resultItem) {
        TestResult testResult = new TestResult();
        testResult.setTestName(resultItem.getTestName());
        if (resultItem.getException() == null){
            testResult.setStatus(SolutionStatus.SUCCESS);
        } else if (ASSERT_CLASS_NAME.equals(resultItem.getException())){
            testResult.setMessage(resultItem.getMessage());
            testResult.setStatus(SolutionStatus.FAILED);
        } else {
            testResult.setMessage(resultItem.getException() + ": " + resultItem.getMessage());
            testResult.setStatus(SolutionStatus.ERROR);
        }

        return testResult;
    }
}
