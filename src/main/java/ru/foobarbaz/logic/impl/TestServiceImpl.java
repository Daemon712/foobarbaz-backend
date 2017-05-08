package ru.foobarbaz.logic.impl;

import org.junit.runner.JUnitCore;
import org.springframework.stereotype.Service;
import ru.foobarbaz.entity.challenge.solution.TestResult;
import ru.foobarbaz.logic.TestService;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TestServiceImpl implements TestService {

    private static final String PREFIX = "foobarbaz";
    private static final String CLASS_NAME_GROUP = "name";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("(public +|)(?:class|interface) +(?<" + CLASS_NAME_GROUP + ">\\w+)");

    @Override
    public List<TestResult> executeTests(String test, String impl) {
        try {
            Path root = Files.createTempDirectory(PREFIX);
            String testClassName = parseClassName(test);
            Path testFile = root.resolve(testClassName);
            Files.write(testFile, test.getBytes());
            Path implFile = root.resolve(parseClassName(impl));
            Files.write(implFile, impl.getBytes());

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, implFile.toString(), testFile.toString());

            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toUri().toURL() });
            Class<?> clazz = Class.forName(testClassName, true, classLoader);

            JUnitCore jUnitCore = new JUnitCore();
            TestResultCollector listener = new TestResultCollector();
            jUnitCore.addListener(listener);
            jUnitCore.run(clazz);

            return listener.getTestResults();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static String parseClassName(String source){
        Matcher m = CLASS_NAME_PATTERN.matcher(source);
        if (!m.find()) throw new IllegalArgumentException("No class definition found in src:\n" + source);
        return m.group("name");
    }
}