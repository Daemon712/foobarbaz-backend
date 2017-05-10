package ru.foobarbaz.logic.impl.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.logic.TestService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {

//    private static final Path APP_TEMP_DIR = Paths.get(System.getProperty("user.home"), ".foobarbaz");
    private static final Path APP_TEMP_DIR = Paths.get("/foobarbaz").toAbsolutePath();
    private static final Path TEST_RUNNER_JAR = APP_TEMP_DIR.resolve("test-runner.jar");

    private static final String CLASS_NAME_GROUP = "name";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public +class +(?<" + CLASS_NAME_GROUP + ">\\w+)");

    private Function<ResultItem, TestResult> converter = new TestRunResultConverter();
    private ObjectReader resultReader = new ObjectMapper().readerFor(Result.class);

    @Override
    public List<TestResult> executeTests(String test, String impl) {
        try {
            if (!Files.exists(APP_TEMP_DIR)) Files.createDirectory(APP_TEMP_DIR);
            Path root = Files.createTempDirectory(APP_TEMP_DIR, "");

            String testClassName = parseClassName(test);
            String implClassName = parseClassName(impl);

            Path testFile = root.resolve(testClassName + ".java");
            Path implFile = root.resolve(implClassName + ".java");
            Files.write(testFile, test.getBytes());
            Files.write(implFile, impl.getBytes());

            String compileCommand = getCompileCommand(root);
            Runtime.getRuntime().exec(compileCommand).waitFor(5, TimeUnit.SECONDS);
            String runTestCommand = getRunTestCommand(root, testClassName);
            Process process = Runtime.getRuntime().exec(runTestCommand);
            byte[] data = read(process.getInputStream());
            process.waitFor(5, TimeUnit.SECONDS);
            Result result =resultReader.readValue(data);
            return result.getItems().stream().map(converter).collect(Collectors.toList());
        } catch (Exception e){
            return Collections.singletonList(new TestResult("*", SolutionStatus.ERROR, e.toString()));
        } finally {
            tryToClearAppDir();
        }
    }

    private byte[] read(InputStream inputStream) throws IOException {
        int len = 1024 * 8;
        byte[] b = new byte[len];
        int total = 0;
        while (total < len) {
            int result = inputStream.read(b, total, len - total);
            if (result == -1) {
                break;
            }
            total += result;
        }
        return b;
    }

    private String getRunTestCommand(Path root, String testClassName) {
        return "java -cp " +
                TEST_RUNNER_JAR + ";" + root +
                " ru.foobarbaz.testrunner.Main " + testClassName;
    }

    private String getCompileCommand(Path root) {
        return "javac -cp " + TEST_RUNNER_JAR + " " + root.toString() + "\\*.java";
    }

    private void tryToClearAppDir(){
        try {
            Files.walk(APP_TEMP_DIR).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    if (!APP_TEMP_DIR.equals(path)) return;
                    if (!TEST_RUNNER_JAR.equals(path)) return;
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                    //TODO log the error
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            //TODO log the error
        }
    }

    private static String parseClassName(String source){
        Matcher m = CLASS_NAME_PATTERN.matcher(source);
        if (!m.find()) throw new IllegalArgumentException("No class definition found in src:\n" + source);
        return m.group(CLASS_NAME_GROUP);
    }
}