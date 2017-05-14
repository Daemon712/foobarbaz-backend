package ru.foobarbaz.logic.impl.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.springframework.stereotype.Service;
import ru.foobarbaz.constant.SolutionStatus;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.exception.CompilationException;
import ru.foobarbaz.logic.TestService;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Arrays;
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

            compile(root, implClassName, impl);
            compile(root, testClassName, test);

            String runTestCommand = getRunTestCommand(root, testClassName);
            Process runTestProcess = Runtime.getRuntime().exec(runTestCommand);
            byte[] data = read(runTestProcess.getInputStream());
            runTestProcess.waitFor(5, TimeUnit.SECONDS);

            Result result =resultReader.readValue(data);
            return result.getItems().stream().map(converter).collect(Collectors.toList());
        } catch (CompilationException e){
            String testName = "compilationError(" + e.getClassName() + ")";
            return Collections.singletonList(new TestResult(testName, SolutionStatus.ERROR, e.getMessage()));
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally {
            tryToClearAppDir();
        }
    }

    private void compile(Path root, String className, String source) throws Exception{
        Path implFile = root.resolve(className + ".java");
        Files.write(implFile, source.getBytes());
        String compileImplCommand = getCompileCommand(root, className);
        Process compileImplProcess = Runtime.getRuntime().exec(compileImplCommand);
        String compileImplError = new String(read(compileImplProcess.getErrorStream()));
        compileImplProcess.waitFor(5, TimeUnit.SECONDS);
        if (!compileImplError.isEmpty()) {
            String message = compileImplError.replace(root.toString(), "").trim();
            throw new CompilationException(className, message);
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
        return Arrays.copyOf(b, total);
    }

    private String getRunTestCommand(Path root, String className) {
        return MessageFormat.format("java -cp {0};{1} ru.foobarbaz.testrunner.Main {2}", TEST_RUNNER_JAR, root, className);
    }

    private String getCompileCommand(Path root, String className) {
        return MessageFormat.format("javac -cp {0};{1} {1}\\{2}.java", TEST_RUNNER_JAR, root, className);
    }

    private void tryToClearAppDir(){
        try {
            Files.walk(APP_TEMP_DIR).sorted(Comparator.reverseOrder()).forEach(path -> {
                try {
                    if (APP_TEMP_DIR.equals(path)) return;
                    if (TEST_RUNNER_JAR.equals(path)) return;
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
        if (!m.find()) throw new CompilationException("...", "No class definition found in src:\n" + source);
        return m.group(CLASS_NAME_GROUP);
    }
}