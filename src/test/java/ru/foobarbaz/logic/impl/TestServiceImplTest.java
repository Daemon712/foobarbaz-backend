package ru.foobarbaz.logic.impl;

import org.junit.Assert;
import org.junit.Test;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.logic.TestService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestServiceImplTest {
    private TestService testService = new TestServiceImpl();

    @Test
    public void testDiv() throws Exception {
        String test = new String(Files.readAllBytes(Paths.get("samples", "test", "DivTest.java")));
        String code = new String(Files.readAllBytes(Paths.get("samples", "impl", "DivImpl.java")));
        List<TestResult> results = testService.executeTests(test, code);
        Assert.assertEquals(results.size(), 4);
        Assert.assertTrue(results.contains(new TestResult("testOneDivOne", SolutionStatus.SUCCESS)));
        Assert.assertTrue(results.contains(new TestResult("testFourDivTwo", SolutionStatus.SUCCESS)));
        Assert.assertTrue(results.contains(new TestResult("testOneDivZero", SolutionStatus.ERROR, "java.lang.ArithmeticException: / by zero")));
        Assert.assertTrue(results.contains(new TestResult("testZeroDivOne", SolutionStatus.FAILED, "expected:<0> but was:<1>")));
    }
}