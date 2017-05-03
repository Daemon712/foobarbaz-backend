package ru.foobarbaz.logic;

import ru.foobarbaz.entity.TestResult;

import java.util.List;

public interface TestService {
    List<TestResult> executeTests(String test, String code);
}
