import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.tooling.BuildException;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.events.FailureResult;
import org.gradle.tooling.events.OperationResult;
import org.gradle.tooling.events.OperationType;
import org.gradle.tooling.events.ProgressListener;
import org.gradle.tooling.events.SkippedResult;
import org.gradle.tooling.events.problems.LineInFileLocation;
import org.gradle.tooling.events.problems.SingleProblemEvent;
import org.gradle.tooling.events.task.TaskFinishEvent;
import org.gradle.tooling.events.task.TaskSuccessResult;
import org.gradle.tooling.events.test.JvmTestOperationDescriptor;
import org.gradle.tooling.events.test.TestFinishEvent;
import org.jspecify.annotations.NonNull;

/**
 * Gradle build/test tool using Gradle Tooling API.
 *
 * <pre><code>
 * Usage:
 *   java GradleTool.java build [-m MODULE] [TASKS...]
 *   java GradleTool.java test [-m MODULE] [-c CLASS] [-t METHOD]
 * </code></pre>
 * <p>
 * Requires gradle-tooling-api JAR in classpath.
 */
void main(String[] args) {
    if (args.length == 0) {
        printUsage();
        System.exit(1);
    }

    var command = args[0];
    var commandArgs = Arrays.copyOfRange(args, 1, args.length);

    try {
        Command cmd = switch (command) {
            case "build" -> BuildCommand.parse(commandArgs);
            case "test" -> TestCommand.parse(commandArgs);
            case "help", "-h", "--help" -> {
                printUsage();
                yield null;
            }
            default -> {
                IO.println("Unknown command: " + command);
                printUsage();
                yield null;
            }
        };

        if (cmd == null) {
            System.exit(0);
        }

        var result = cmd.execute();
        System.exit(result.exitCode());
    } catch (IllegalArgumentException e) {
        IO.println("Error: " + e.getMessage());
        printUsage();
        System.exit(1);
    } catch (Exception e) {
        IO.println("Error: " + e.getMessage());
        if (System.getenv("DEBUG") != null) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        System.exit(1);
    }
}

// ========== Commands ==========

sealed interface Command permits BuildCommand, TestCommand {

    Path projectDir();

    Optional<String> module();

    boolean verbose();

    boolean stacktrace();

    boolean noConfigCache();

    Result execute();

    default List<String> buildGradleArgs() {
        var args = new ArrayList<>(List.of("--no-problems-report", "--continue", "--exclude-task", "detekt"));
        if (stacktrace()) {
            args.add("--stacktrace");
        }
        if (noConfigCache()) {
            args.add("--no-configuration-cache");
        }
        return args;
    }
}

record BuildCommand(
        Path projectDir,
        List<String> tasks,
        Optional<String> module,
        boolean verbose,
        boolean stacktrace,
        boolean noConfigCache
) implements Command {

    static BuildCommand parse(String[] args) {
        Path projectDir = Path.of(System.getProperty("user.dir"));
        Optional<String> module = Optional.empty();
        boolean verbose = false;
        boolean stacktrace = false;
        boolean noConfigCache = false;
        List<String> tasks = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            var arg = args[i];
            switch (arg) {
                case "-m", "--module" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("--module requires value");
                    }
                    module = Optional.of(args[++i]);
                }
                case "-v", "--verbose" -> verbose = true;
                case "-s", "--stacktrace" -> stacktrace = true;
                case "--no-configuration-cache", "--no-config-cache" -> noConfigCache = true;
                case "-h", "--help" -> {
                    printBuildHelp();
                    System.exit(0);
                }
                default -> {
                    if (!arg.startsWith("-")) {
                        tasks.add(arg);
                    } else {
                        throw new IllegalArgumentException("Unknown option: " + arg);
                    }
                }
            }
        }

        if (tasks.isEmpty()) {
            tasks = List.of("clean", "classes", "testClasses");
        }

        return new BuildCommand(projectDir, tasks, module, verbose, stacktrace, noConfigCache);
    }

    @Override
    public Result execute() {
        List<String> gradleArgs = buildGradleArgs();
        List<String> taskArgs = module()
                .map(m -> tasks().stream().map(t -> m + ":" + t).toList())
                .orElseGet(() -> new ArrayList<>(tasks()));

        return GradleExecutor.execute(this, gradleArgs, taskArgs, "build");
    }
}

record TestCommand(
        Path projectDir,
        Optional<String> module,
        Optional<String> testClass,
        Optional<String> testMethod,
        boolean rerun,
        boolean verbose,
        boolean stacktrace,
        boolean noConfigCache
) implements Command {

    static TestCommand parse(String[] args) {
        var projectDir = Path.of(System.getProperty("user.dir"));
        Optional<String> module = Optional.empty();
        Optional<String> testClass = Optional.empty();
        Optional<String> testMethod = Optional.empty();
        boolean rerun = false;
        boolean verbose = false;
        boolean stacktrace = false;
        boolean noConfigCache = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "-m", "--module" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("--module requires value");
                    }
                    module = Optional.of(args[++i]);
                }
                case "-c", "--class" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("--class requires value");
                    }
                    testClass = Optional.of(args[++i]);
                }
                case "-t", "--test" -> {
                    if (i + 1 >= args.length) {
                        throw new IllegalArgumentException("--test requires value");
                    }
                    testMethod = Optional.of(args[++i]);
                }
                case "-r", "--rerun" -> rerun = true;
                case "-v", "--verbose" -> verbose = true;
                case "-s", "--stacktrace" -> stacktrace = true;
                case "--no-configuration-cache", "--no-config-cache" -> noConfigCache = true;
                case "-h", "--help" -> {
                    printTestHelp();
                    System.exit(0);
                }
                default -> {
                    if (!arg.startsWith("-")) {
                        // Treat as test class if not specified
                        if (testClass.isEmpty()) {
                            testClass = Optional.of(arg);
                        }
                    } else {
                        throw new IllegalArgumentException("Unknown option: " + arg);
                    }
                }
            }
        }

        if (testMethod.isPresent() && testClass.isEmpty()) {
            throw new IllegalArgumentException("--test requires --class");
        }

        return new TestCommand(projectDir, module, testClass, testMethod, rerun, verbose, stacktrace, noConfigCache);
    }

    @Override
    public Result execute() {
        List<String> gradleArgs = buildGradleArgs();

        if (rerun()) {
            gradleArgs.add("--rerun-tasks");
        }

        String testTask = module().map(m -> m + ":test").orElse("test");

        // Use newTestLauncher() for class/method filtering (withTestsFor API)
        // Use newBuild() for running all tests in a module (forTasks properly scoped)
        if (testClass().isPresent()) {
            return GradleExecutor.executeTest(this, gradleArgs, testTask);
        }
        return GradleExecutor.execute(this, gradleArgs, List.of(testTask), "test");
    }
}

// ========== Gradle Executor ==========

static class GradleExecutor {

    static Result execute(Command cmd, List<String> gradleArgs, List<String> taskArgs, String commandName) {
        var context = new ExecutionContext();

        try (var connection = connect(cmd.projectDir())) {
            connection.newBuild()
                    .withArguments(gradleArgs)  // Only pass flags, not task names
                    .forTasks(taskArgs.toArray(String[]::new))
                    .setStandardOutput(context.collector.stdout())
                    .setStandardError(context.collector.stderr())
                    .addProgressListener(taskListener(context.taskEvents), OperationType.TASK)
                    .addProgressListener(testListener(context.testEvents), OperationType.TEST)
                    .addProgressListener(problemListener(context.problems), OperationType.PROBLEMS)
                    .run();
        } catch (BuildException e) {
            context.fail(e);
        }

        var result = context.toResult(Map.of());
        OutputFormatter.format(result, cmd, commandName, taskArgs);
        return result;
    }

    @SuppressWarnings("UnstableApiUsage")
    static Result executeTest(TestCommand cmd, List<String> gradleArgs, String testTask) {
        var context = new ExecutionContext();

        try (var connection = connect(cmd.projectDir())) {
            var launcher = connection.newTestLauncher()
                    .withArguments(gradleArgs)
                    .setStandardOutput(context.collector.stdout())
                    .setStandardError(context.collector.stderr())
                    .addProgressListener(taskListener(context.taskEvents), OperationType.TASK)
                    .addProgressListener(testListener(context.testEvents), OperationType.TEST)
                    .addProgressListener(problemListener(context.problems), OperationType.PROBLEMS);

            // withTestsFor API (Gradle 7.6+) - includeClass/Method require FQCN, use pattern for simple names
            launcher.withTestsFor(specs -> {
                var spec = specs.forTaskPath(testTask);
                var className = cmd.testClass().orElseThrow();
                boolean isFqcn = className.contains(".");
                var methodOpt = cmd.testMethod();

                if (isFqcn) {
                    methodOpt.ifPresentOrElse(
                            method -> spec.includeMethod(className, method),
                            () -> spec.includeClass(className));
                } else {
                    // Use ** to match any package depth (e.g., "**ClassName" matches "a.b.c.ClassName")
                    String pattern = className.startsWith("*") ? className : "**." + className;
                    spec.includePattern(pattern + methodOpt.map(m -> "." + m).orElse(""));
                }
            });

            launcher.run();
        } catch (Exception e) {
            context.fail(e);
        }

        var result = context.toResult(buildTestErrorMetadata(context, cmd));
        OutputFormatter.format(result, cmd, "test", List.of(testTask));
        return result;
    }

    // ========== Helper Methods ==========

    private static ProjectConnection connect(Path projectDir) {
        return GradleConnector.newConnector()
                .forProjectDirectory(projectDir.toFile())
                .connect();
    }

    private static ProgressListener taskListener(List<TaskEvent> events) {
        return event -> {
            if (event instanceof TaskFinishEvent taskFinish) {
                var desc = taskFinish.getDescriptor();
                var status = resolveTaskStatus(taskFinish.getResult());
                events.add(new TaskEvent(desc.getTaskPath(), status));
            }
        };
    }

    private static String resolveTaskStatus(OperationResult result) {
        return switch (result) {
            case FailureResult _ -> "FAILED";
            case SkippedResult _ -> "SKIPPED";
            case TaskSuccessResult s when s.isUpToDate() -> "UP_TO_DATE";
            case TaskSuccessResult s when s.isFromCache() -> "FROM_CACHE";
            case TaskSuccessResult _ -> "EXECUTED";
            case null, default -> "SUCCESS";
        };
    }

    private static ProgressListener testListener(List<TestEvent> events) {
        return event -> {
            if (event instanceof TestFinishEvent testFinish
                    && testFinish.getDescriptor() instanceof JvmTestOperationDescriptor jvmDesc
                    && jvmDesc.getMethodName() != null
                    && testFinish.getResult() instanceof FailureResult failureResult) {

                var failures = failureResult.getFailures();
                var message = failures.isEmpty() ? "Test failed" : failures.getFirst().getMessage();
                events.add(new TestEvent(jvmDesc.getClassName(), jvmDesc.getMethodName(), message));
            }
        };
    }

    private static Map<String, Object> buildTestErrorMetadata(ExecutionContext context, TestCommand cmd) {
        if (context.exception == null) {
            return Map.of();
        }

        var errorMsg = context.exception.getMessage();
        var errorValue = errorMsg != null ? errorMsg : context.exception.getClass().getName();

        // Check for "no matching tests" in output (only relevant when class filter is specified)
        if (cmd.testClass().isPresent()) {
            boolean noMatchingTests = context.collector.getLines().stream()
                    .anyMatch(line -> line.contains("No matching tests"));
            if (noMatchingTests) {
                var testName = cmd.testClass().get() + cmd.testMethod().map(m -> "." + m).orElse("");
                return Map.of("exception", errorValue, "hint", "Test not found: " + testName);
            }
        }
        return Map.of("exception", errorValue);
    }

    // ========== Execution Context ==========

    private static class ExecutionContext {

        final OutputCollector collector = new OutputCollector();
        final List<TaskEvent> taskEvents = new CopyOnWriteArrayList<>();
        final List<TestEvent> testEvents = new CopyOnWriteArrayList<>();
        final List<ProblemInfo> problems = new CopyOnWriteArrayList<>();
        Exception exception;

        void fail(Exception e) {
            this.exception = e;
        }

        Result toResult(Map<String, Object> metadata) {
            int exitCode = exception != null ? 1 : 0;
            var finalMetadata = metadata.isEmpty() && exception != null
                    ? Map.<String, Object>of("exception", exception.getMessage())
                    : metadata;
            return new Result(exitCode, collector.getLines(), taskEvents, testEvents, problems, finalMetadata);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    private static ProgressListener problemListener(List<ProblemInfo> problems) {
        return event -> {
            if (event instanceof SingleProblemEvent problemEvent) {
                var problem = problemEvent.getProblem();
                var definition = problem.getDefinition();
                var fileLoc = problem.getOriginLocations().stream()
                        .filter(LineInFileLocation.class::isInstance)
                        .map(LineInFileLocation.class::cast)
                        .findFirst()
                        .orElse(null);

                int severityLevel = definition.getSeverity().getSeverity();
                var severity = severityLevel >= 2 ? "ERROR" : severityLevel == 1 ? "WARNING" : "ADVICE";
                problem.getContextualLabel();
                var label = problem.getContextualLabel().getContextualLabel();
                var detailsObj = problem.getDetails();
                var message = label + ": " + detailsObj.getDetails();

                problems.add(new ProblemInfo(severity,
                        fileLoc != null ? fileLoc.getPath() : null,
                        fileLoc != null ? fileLoc.getLine() : 0,
                        message));
            }
        };
    }
}

// ========== Output Collection ==========

record TaskEvent(String path, String status) {}

record TestEvent(String className, String methodName, String failureMessage) {}

record ProblemInfo(String severity, String file, int line, String message) {}

static class OutputCollector {

    private final List<String> lines = new CopyOnWriteArrayList<>();

    OutputStream stdout() {
        return new LineCapturingOutputStream(lines, "");
    }

    OutputStream stderr() {
        return new LineCapturingOutputStream(lines, "[ERR] ");
    }

    List<String> getLines() {
        return new ArrayList<>(lines);
    }

    private static class LineCapturingOutputStream extends OutputStream {

        private final List<String> lines;
        private final String prefix;
        private final ByteArrayOutputStream lineBuffer = new ByteArrayOutputStream();

        LineCapturingOutputStream(List<String> lines, String prefix) {
            this.lines = lines;
            this.prefix = prefix;
        }

        @Override
        public void write(int b) {
            lineBuffer.write(b);

            if (b == '\n') {
                flushLine();
            }
        }

        @Override
        public void write(byte @NonNull [] b, int off, int len) {
            for (int i = off; i < off + len; i++) {
                write(b[i]);
            }
        }

        private void flushLine() {
            var line = lineBuffer.toString(StandardCharsets.UTF_8).trim();
            if (!line.isEmpty()) {
                lines.add(prefix + line);
            }
            lineBuffer.reset();
        }

        @Override
        public void flush() {
            if (lineBuffer.size() > 0) {
                flushLine();
            }
        }

        @Override
        public void close() {
            flush();
        }
    }
}

// ========== Result ==========

record Result(
        int exitCode,
        List<String> outputLines,
        List<TaskEvent> taskEvents,
        List<TestEvent> testEvents,
        List<ProblemInfo> problems,
        Map<String, Object> metadata
) {}

// ========== Error Analysis ==========

static class ErrorAnalyzer {

    // Patterns for error detection (handle optional [ERR] prefix from stderr)
    static final Pattern JAVA_ERROR = Pattern.compile("(?:\\[ERR] )?(.+\\.java):(\\d+):\\s*error:\\s*(.+)");
    static final Pattern JAVA_SYMBOL = Pattern.compile("(?:\\[ERR] )?\\s*symbol:\\s*(.+)");
    static final Pattern KOTLIN_ERROR = Pattern.compile("(?:\\[ERR] )?e:\\s*file://(.+\\.kt):(\\d+):(\\d+)\\s+(.+)");
    static final Pattern KOTLIN_ERROR_ALT = Pattern.compile("(?:\\[ERR] )?(.+\\.kt):(\\d+):(\\d+):\\s*(.+)");

    record ErrorInfo(String file, int line, String message) {}

    static List<ErrorInfo> analyzeErrors(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return List.of();
        }

        List<ErrorInfo> errors = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            // Java compilation errors
            var javaMatcher = JAVA_ERROR.matcher(line);
            if (javaMatcher.find()) {
                String message = javaMatcher.group(3);
                // Look ahead for symbol details (e.g., "symbol: class UndefinedType")
                if (i + 1 < lines.size()) {
                    var symbolMatcher = JAVA_SYMBOL.matcher(lines.get(i + 1));
                    if (symbolMatcher.find()) {
                        message = message + " - " + symbolMatcher.group(1).trim();
                    }
                }
                errors.add(new ErrorInfo(javaMatcher.group(1), Integer.parseInt(javaMatcher.group(2)), message));
                continue;
            }

            // Kotlin compilation errors
            var kotlinMatcher = KOTLIN_ERROR.matcher(line);
            if (kotlinMatcher.find()) {
                errors.add(new ErrorInfo(kotlinMatcher.group(1), Integer.parseInt(kotlinMatcher.group(2)),
                        kotlinMatcher.group(4)));
                continue;
            }

            var kotlinAltMatcher = KOTLIN_ERROR_ALT.matcher(line);
            if (kotlinAltMatcher.find()) {
                errors.add(new ErrorInfo(kotlinAltMatcher.group(1), Integer.parseInt(kotlinAltMatcher.group(2)),
                        kotlinAltMatcher.group(4)));
            }
        }

        return errors;
    }

    /**
     * Find test reports based on executed test tasks. Uses task paths from taskEvents to locate build directories
     * directly, avoiding expensive recursive filesystem searches.
     */
    static List<Path> findTestReports(Path projectDir, List<TaskEvent> taskEvents) {
        // Extract executed test tasks (tasks ending with :test that were actually executed)
        var testTaskPaths = taskEvents.stream()
                .filter(e -> e.path().endsWith(":test"))
                .filter(e -> "EXECUTED".equals(e.status()) || "FAILED".equals(e.status()))
                .map(TaskEvent::path)
                .distinct()
                .toList();

        if (testTaskPaths.isEmpty()) {
            return List.of();
        }

        List<Path> reports = new ArrayList<>();
        for (String taskPath : testTaskPaths) {
            // Convert task path to build directory: ":module:submodule:test" -> "module/submodule/build"
            Path testResultsDir = resolveTestResultsDir(projectDir, taskPath);
            if (Files.isDirectory(testResultsDir)) {
                try (Stream<Path> files = Files.list(testResultsDir)) {
                    files.filter(p -> p.toString().endsWith(".xml"))
                            .forEach(reports::add);
                } catch (IOException e) {
                    if (System.getenv("DEBUG") != null) {
                        System.err.println("[DEBUG] Failed to read test results: " + e.getMessage());
                    }
                }
            }
        }
        return reports;
    }

    private static Path resolveTestResultsDir(Path projectDir, String taskPath) {
        // ":test" -> "" (root project)
        // ":module:test" -> "module"
        // ":module:submodule:test" -> "module/submodule"
        String modulePath = taskPath.substring(0, taskPath.lastIndexOf(":test"));
        if (modulePath.startsWith(":")) {
            modulePath = modulePath.substring(1);
        }
        modulePath = modulePath.replace(":", "/");

        Path moduleDir = modulePath.isEmpty() ? projectDir : projectDir.resolve(modulePath);
        return moduleDir.resolve("build/test-results/test");
    }
}

// ========== Output Formatting ==========

static class OutputFormatter {

    static void format(Result result, Command cmd, String commandName, List<String> taskArgs) {
        IO.println("=== GRADLE_RESULT ===");
        IO.println("STATUS: " + (result.exitCode() == 0 ? "SUCCESS" : "FAILED"));
        IO.println("COMMAND: " + commandName);
        IO.println("PROJECT_DIR: " + cmd.projectDir().toAbsolutePath());
        cmd.module().ifPresent(m -> IO.println("MODULE: " + m));
        IO.println("TASKS: " + String.join(", ", taskArgs));
        IO.println("");

        // Build summary from task events
        if (!result.taskEvents().isEmpty()) {
            var statusCounts = result.taskEvents().stream()
                    .collect(Collectors.groupingBy(TaskEvent::status, Collectors.counting()));
            IO.println("[BUILD_SUMMARY]");
            statusCounts.forEach((status, count) -> IO.println(status + ": " + count));
            IO.println("");
        }

        // Error from metadata (e.g., test not found)
        if (result.exitCode() != 0 && !result.metadata().isEmpty()) {
            IO.println("[ERROR]");
            if (result.metadata().containsKey("hint")) {
                IO.println("HINT: " + result.metadata().get("hint"));
            }
            if (result.metadata().containsKey("exception")) {
                IO.println("DETAIL: " + result.metadata().get("exception"));
            }
            IO.println("");
        }

        // Task events
        var failedTasks = result.taskEvents().stream()
                .filter(e -> "FAILED".equals(e.status()))
                .toList();
        if (!failedTasks.isEmpty()) {
            IO.println("[FAILED_TASKS]");
            for (int i = 0; i < failedTasks.size(); i++) {
                IO.println("TASK_" + (i + 1) + ": " + failedTasks.get(i).path());
            }
            IO.println("");
        }

        // Test failures (testEvents only contains failures)
        if (!result.testEvents().isEmpty()) {
            IO.println("[TEST_FAILURES]");
            int i = 1;
            for (var test : result.testEvents()) {
                IO.println("FAILURE_" + i++ + ":");
                IO.println("  CLASS: " + test.className());
                IO.println("  METHOD: " + test.methodName());
                IO.println("  MESSAGE: " + test.failureMessage());
                IO.println("");
            }
        }

        // Problems from Gradle Problem API (preferred) or fallback to output parsing
        if (result.exitCode() != 0) {
            var problems = result.problems().stream()
                    .filter(p -> "ERROR".equals(p.severity()))
                    .toList();

            if (!problems.isEmpty()) {
                IO.println("[PROBLEMS]");
                for (int i = 0; i < problems.size(); i++) {
                    var problem = problems.get(i);
                    var location = problem.file() != null
                            ? problem.file() + ":" + problem.line()
                            : "(unknown location)";
                    IO.println("PROBLEM_" + (i + 1) + ": " + location);
                    IO.println("  " + problem.message());
                    IO.println("");
                }
            } else {
                // Fallback to output parsing if Problem API didn't capture errors
                var errors = ErrorAnalyzer.analyzeErrors(result.outputLines());
                if (!errors.isEmpty()) {
                    IO.println("[COMPILE_ERRORS]");
                    for (int i = 0; i < errors.size(); i++) {
                        var err = errors.get(i);
                        IO.println("ERROR_" + (i + 1) + ": " + err.file() + ":" + err.line());
                        IO.println("  " + err.message());
                        IO.println("");
                    }
                }
            }
        }

        // Test reports (for test command) - use executed task paths instead of filesystem search
        if ("test".equals(commandName)) {
            var reports = ErrorAnalyzer.findTestReports(cmd.projectDir(), result.taskEvents());
            if (!reports.isEmpty()) {
                IO.println("[TEST_REPORTS]");

                // Group by parent directory
                var grouped = reports.stream()
                        .collect(Collectors.groupingBy(Path::getParent));

                int reportNum = 1;
                for (var entry : grouped.entrySet()) {
                    var relPath = cmd.projectDir().relativize(entry.getKey());
                    IO.println("REPORT_" + reportNum++ + ": " + relPath + " (" + entry.getValue().size() + " files)");
                    for (var file : entry.getValue()) {
                        IO.println("  - " + file.getFileName());
                    }
                }
                IO.println("");
            }
        }

        // Gradle output (verbose only - structured sections above are sufficient for failures)
        if (cmd.verbose() && !result.outputLines().isEmpty()) {
            IO.println("[GRADLE_OUTPUT]");
            result.outputLines().forEach(IO::println);
            IO.println("");
        }

        IO.println("=== END_GRADLE_RESULT ===");
    }
}

// ========== Help ==========

static void printUsage() {
    IO.println("""
            Usage: java GradleTool.java <command> [options]

            Commands:
              build   Build the project
              test    Run tests
              help    Show this help

            Use 'java GradleTool.java <command> --help' for command-specific help.
            """);
}

static void printBuildHelp() {
    IO.println("""
            Usage: java GradleTool.java build [OPTIONS] [TASKS...]

            Build the project using Gradle Tooling API.

            OPTIONS:
              -m, --module <MODULE>       Target specific module (e.g., :spring-webflux-api)
              -v, --verbose               Show full Gradle output
              -s, --stacktrace            Enable stacktrace
              --no-configuration-cache    Disable configuration cache
              -h, --help                  Show this help

            TASKS: (default: clean classes testClasses)
              clean, build, assemble, classes, testClasses, ...

            EXAMPLES:
              java GradleTool.java build
              java GradleTool.java build -m :spring-webflux-api
              java GradleTool.java build clean build
            """);
}

static void printTestHelp() {
    IO.println("""
            Usage: java GradleTool.java test [OPTIONS]

            Run tests using Gradle Tooling API.

            OPTIONS:
              -m, --module <MODULE>       Target specific module (e.g., :spring-webflux-api)
              -c, --class <CLASS>         Test class pattern
              -t, --test <METHOD>         Test method (requires --class)
              -r, --rerun                 Force rerun (--rerun-tasks)
              -v, --verbose               Show full Gradle output
              -s, --stacktrace            Enable stacktrace
              --no-configuration-cache    Disable configuration cache
              -h, --help                  Show this help

            EXAMPLES:
              java GradleTool.java test
              java GradleTool.java test -m :spring-webflux-api
              java GradleTool.java test -m :spring-webflux-api -c PersonControllerTest
              java GradleTool.java test -c PersonControllerTest -t shouldReturnPerson
              java GradleTool.java test --rerun
            """);
}
