package ru.foobarbaz.logic.impl;

import org.junit.Assert;
import org.junit.Test;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.logic.TestService;
import ru.foobarbaz.logic.impl.test.TestServiceImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestServiceImplTest {
    private TestService testService = new TestServiceImpl();

    @Test
    public void testSimpleImpl() throws Exception {
        String test = new String(Files.readAllBytes(Paths.get("samples", "test", "DivTest.java")));
        String code = new String(Files.readAllBytes(Paths.get("samples", "impl", "DivImpl.java")));
        List<TestResult> results = testService.executeTests(test, code);
        results.forEach(System.out::println);
        Assert.assertEquals(4, results.size());
        Assert.assertTrue(results.contains(new TestResult("testOneDivOne(DivTest)", SolutionStatus.SUCCESS)));
        Assert.assertTrue(results.contains(new TestResult("testFourDivTwo(DivTest)", SolutionStatus.SUCCESS)));
        Assert.assertTrue(results.contains(new TestResult("testOneDivZero(DivTest)", SolutionStatus.ERROR, "java.lang.ArithmeticException: / by zero")));
        Assert.assertTrue(results.contains(new TestResult("testZeroDivOne(DivTest)", SolutionStatus.FAILED, "expected:<1> but was:<0>")));
    }

    @Test
    public void testEmptyImpl() throws Exception {
        String test = new String(Files.readAllBytes(Paths.get("samples", "test", "DivTest.java")));
        String code = "...";
        List<TestResult> results = testService.executeTests(test, code);
        results.forEach(System.out::println);
        Assert.assertEquals(1, results.size());
        String message = "No class definition found in src:\n...";
        Assert.assertTrue(results.contains(new TestResult("compilationError(...)", SolutionStatus.ERROR, message)));
    }

    @Test
    public void testBadImpl() throws Exception {
        String test = new String(Files.readAllBytes(Paths.get("samples", "test", "DivTest.java")));
        String code = "public class DivImpl";
        List<TestResult> results = testService.executeTests(test, code);
        results.forEach(System.out::println);
        Assert.assertEquals(1, results.size());
        String message = "\\DivImpl.java:1: error: reached end of file while parsing\r\n" +
                "public class DivImpl\r\n" +
                "                    ^\r\n" +
                "1 error";
        Assert.assertTrue(results.contains(new TestResult("compilationError(DivImpl)", SolutionStatus.ERROR, message)));
    }
}