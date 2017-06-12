package ru.foobarbaz.logic.impl.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.exception.CompilationException;
import ru.foobarbaz.logic.TestService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestServiceImpl.class);

    private static final Path APP_DIR = Paths.get(System.getProperty("user.home"), ".foobarbaz");
    private static final Path TEST_RUNNER_JAR = APP_DIR.resolve("test-runner.jar");

    private static final String CLASS_NAME_GROUP = "name";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public +class +(?<" + CLASS_NAME_GROUP + ">\\w+)");
    private static final Pattern RESULT_PATTERN = Pattern.compile("\\{.+}");

    private Function<ResultItem, TestResult> converter = new TestRunResultConverter();
    private ObjectReader resultReader = new ObjectMapper().readerFor(Result.class);

    @Override
    public List<TestResult> executeTests(String test, String impl) {
        LOGGER.trace("executeTests.\nTest:\n{}\n\nImpl:\n{}\n", test, impl);
        Path temp = null;
        try {
            if (!Files.exists(APP_DIR)) Files.createDirectory(APP_DIR);
            temp = Files.createTempDirectory(APP_DIR, "");
            LOGGER.trace("temp directory {} has been created", temp);

            String testClassName = parseClassName(test);
            LOGGER.trace("testClassName = {}", testClassName);
            String implClassName = parseClassName(impl);
            LOGGER.trace("implClassName = {}", implClassName);

            compile(temp, implClassName, impl);
            compile(temp, testClassName, test);

            String runTestCommand = getRunTestCommand(temp, testClassName);
            LOGGER.trace("execute command {}", runTestCommand);
            Process runTestProcess = Runtime.getRuntime().exec(runTestCommand);
            runTestProcess.waitFor(3, TimeUnit.SECONDS);
            String data = new String(read(runTestProcess.getInputStream()));
            Matcher matcher = RESULT_PATTERN.matcher(data);
            while (matcher.find()) data = matcher.group();

            LOGGER.trace("test result:\n{}\n", data);
            Result result = resultReader.readValue(data);
            return result.getItems().stream().map(converter).collect(Collectors.toList());
        } catch (CompilationException e) {
            String testName = "compilationError(" + e.getClassName() + ")";
            //noinspection ArraysAsListWithZeroOrOneArgument
            return Arrays.asList(new TestResult(testName, SolutionStatus.ERROR, e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (temp != null) tryCleanTemp(temp);
        }
    }

    private void compile(Path root, String className, String source) throws Exception {
        Path implFile = root.resolve(className + ".java");
        Files.write(implFile, source.getBytes());
        String compileCommand = getCompileCommand(root, className);
        LOGGER.trace("execute command {}", compileCommand);
        Process compileProcess = Runtime.getRuntime().exec(compileCommand);
        compileProcess.waitFor(3, TimeUnit.SECONDS);
        String compileError = new String(read(compileProcess.getErrorStream()));
        if (!compileError.isEmpty()) {
            String message = compileError.replace(root.toString(), "").trim();
            LOGGER.warn("class {} has compilation error: {}", className, compileError);
            throw new CompilationException(className, message);
        }
        LOGGER.trace("class {} has been compiled", className);
    }

    private byte[] read(InputStream inputStream) throws IOException {
        int len = inputStream.available();
        byte[] b = new byte[len];
        int total = 0;
        while (total < len) {
            int result = inputStream.read(b, total, len - total);
            if (result == -1) {
                break;
            }
            total += result;
        }
        return Arrays.copyOf(b, total);
    }

    private String getRunTestCommand(Path root, String className) {
        String classpath = buildClassPath(root);
        return MessageFormat.format("java -cp {0} ru.foobarbaz.testrunner.Main {1}", classpath, className);
    }

    private String getCompileCommand(Path root, String className) {
        String classpath = buildClassPath(root);
        String fullClassName = root.resolve(className) + ".java";
        return MessageFormat.format("javac -cp {0} {1}", classpath, fullClassName);
    }

    private String buildClassPath(Path root) {
        return new StringJoiner(File.pathSeparator)
                .add(TEST_RUNNER_JAR.toString())
                .add(root.toString())
                .toString();
    }

    private void tryCleanTemp(Path root) {
        try {
            Files.walk(root).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    LOGGER.error("error during delete {}", path);
                }
            });
        } catch (IOException e) {
            LOGGER.error("error during walk {}", root);
        }
    }

    private static String parseClassName(String source) {
        Matcher m = CLASS_NAME_PATTERN.matcher(source);
        if (!m.find()) throw new CompilationException("...", "No class definition found in src:\n" + source);
        return m.group(CLASS_NAME_GROUP);
    }
}