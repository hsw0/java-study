---
name: gradle-build
description: |
  Gradle build and test execution skill. Summarizes build/test results and provides report paths.
  Use when: (1) Gradle project builds (build, assemble, clean), (2) Running tests and checking results,
  (3) Building/testing specific modules, (4) Analyzing compile errors, (5) Debugging test failures.
context: fork
model: haiku
allowed-tools: Read Grep Glob Bash(./scripts/gradle-tool.sh:*)
---

# Gradle Build Skill

## CRITICAL: Your Role as Subagent

You are running as a **subagent** invoked by the parent agent. Your role is to:

1. **Execute** the requested build/test operation
2. **Analyze** the results thoroughly
3. **Report** findings back to the parent agent with clarity

Your task is:
```
$ARGUMENTS
```

**If Your task specification is empty or not specified, perform a full project build by running `scripts/gradle-tool.sh build` without any arguments.**


### What You MUST Do

**Step 1: Execute the command**
- Run `gradle-tool.sh build` or `gradle-tool.sh test` based on the task
- The tool outputs structured results with sections: `[BUILD_SUMMARY]`, `[TEST_FAILURES]`, `[TEST_REPORTS]`

**Step 2: On SUCCESS**
- Report summary: task counts, test counts (run/passed/skipped)

**Step 3: On FAILURE - Analyze errors**

**For compilation errors:**
- Check `[COMPILE_ERRORS]` section
- Format: `ERROR_N: /path/to/file:line` followed by indented error message
- Extract: file path, line number, error description

**For test failures:**
1. Check `[TEST_FAILURES]` section for overview (CLASS, METHOD, MESSAGE)
2. Check `[TEST_REPORTS]` section for XML file paths
3. **Use Read tool** to read each XML file listed in `[TEST_REPORTS]`
4. In the XML, find `<testcase>` elements with `<failure>` or `<error>` children
5. From each `<failure>` element, extract:
   - `message` attribute (error description)
   - `type` attribute (exception class name)
   - Text content between tags (stack trace with file:line references)
6. Parse the stack trace to find source file path and line number
   - Example: `at full.qualifed.SomeClassNameTests.testMethodName(SomeClassNameTests.kt:21)`
   - Extract: `SomeClassNameTests.kt:21`

**Step 4: Report to parent agent**
- Follow the Response Format section below
- Include all extracted information: file paths, line numbers, messages, stack traces

### What You MUST NOT Do

- **DO NOT** attempt to fix code or suggest fixes
- **DO NOT** edit any source files
- **DO NOT** make assumptions about what the parent wants to do next
- **DO NOT** run additional builds/tests beyond what was requested

### Response Format

Structure your response as a clear report:

```markdown
## Build/Test Result: [SUCCESS/FAILED]

### Summary
- **Command**: test (or build)
- **Status**: <N> failure(s) among <M> tests executed (or compilation failed)
- **Tasks executed**: <count>
- **Tasks up-to-date**: <count>
- **Tasks skipped**: <count>

### Error Analysis

**Error in `<module>:<test-or-source-class>`**
- **Type**: Compilation Error | Test Failure | Runtime Exception
- **File**: <file-path>:<line-number> (extracted from COMPILE_ERRORS or stack trace)
- **Test Method**: <method()> (if test failure)
- **Exception**: <exception.type> (from XML `type` attribute)
- **Message**: <error or assertion message>
- **Stack Trace**: (from XML `<failure>` element text content)
```
<full stack trace with file:line references>
```

This indicates <brief explanation of what the error means>.

### Test Reports

Test reports are available at:
1. <module>/build/test-results/test/TEST-<Class1>.xml
2. <module>/build/test-results/test/TEST-<Class2>.xml

### Failing Test File
The failing test is located at: <source-file-path> (extracted from stack trace)

### Recommended Next Steps for Parent Agent
- Read <source-file-path> to examine the failing test/code at line <line-number>
- Review the test XML report for detailed failure context
```

---

## Gradle Tool (scripts/gradle-tool.sh)

Uses Gradle Tooling API for structured output optimized for AI parsing.

NOTE: scripts directory is located at `$WORKSPACE/.claude/skills/gradle-build/scripts/`.

### Build Command

NOTE: full script path omitted for brevity. Always use full path for Bash tool.

```bash
# Build entire project (recommended)
scripts/gradle-tool.sh build

# Build specific module
scripts/gradle-tool.sh build -m :spring-webflux-api

# Build specific module with custom tasks
scripts/gradle-tool.sh build -m :spring-webflux-api classes

# Custom tasks
scripts/gradle-tool.sh build clean classes testClasses

# Assemble without tests
scripts/gradle-tool.sh build assemble
```

Options:
- `-m, --module MODULE` : Target specific module - Gradle subproject path (e.g., `:spring-webflux-api`, `:module:entityid`)
- `-v, --verbose` : Show full gradle output (only for Gradle infrastructure issues)
- `-s, --stacktrace` : Show stacktrace (only for Gradle infrastructure issues)
- `--no-configuration-cache` : Bypass configuration cache (for cache-related issues)

Recommended way of compiling entire project is just running `scripts/gradle-tool.sh build` without any arguments.
You don't need to specify compileJava, compileKotlin, classes, -s, ...

### Test Command

```bash
# Run all tests
scripts/gradle-tool.sh test

# Test specific module
scripts/gradle-tool.sh test -m :spring-webflux-api

# Run specific test class
scripts/gradle-tool.sh test -m :spring-webflux-api -c PersonControllerTest

# Run specific test method
scripts/gradle-tool.sh test -m :spring-webflux-api -c PersonControllerTest -t shouldReturnPerson

# Force rerun ignoring cache
scripts/gradle-tool.sh test -m :module:entityid --rerun
```

Options:
- `-m, --module MODULE` : Target specific module
- `-c, --class CLASS` : Specific test class
- `-t, --test METHOD` : Specific test method (requires `-c`)
- `-r, --rerun` : Rerun ignoring cache
- `-v, --verbose` : Show full gradle output (only for Gradle infrastructure issues)
- `-s, --stacktrace` : Show stacktrace (only for Gradle infrastructure issues)
- `--no-configuration-cache` : Bypass configuration cache (for cache-related issues)


## Output Format

The tool outputs structured data optimized for AI parsing:

```
=== GRADLE_RESULT ===
STATUS: SUCCESS | FAILED
COMMAND: build | test
PROJECT_DIR: /path/to/workspace
MODULE: :spring-webflux-api (if applicable)
TASKS: clean, classes, testClasses

[BUILD_SUMMARY]
ACTIONABLE: 42
EXECUTED: 35
UPTODATE: 7

[ERROR_ANALYSIS]  (only on failure)
TOTAL_ERRORS: 2

ERROR_1:
  TYPE: COMPILATION_ERROR
  FILE: /path/to/workspace/module/entityid/src/main/java/Foo.java
  LINE: 42
  MESSAGE: cannot find symbol: class Bar

ERROR_2:
  TYPE: TEST_FAILURE
  FILE: PersonControllerTest.java
  LINE: 0
  MESSAGE: Test failed: shouldReturnPerson

[TEST_REPORTS]  (only for test command)
REPORT_1: module/entityid/build/test-results/test (2 files)
  - TEST-EntityIdFactoryTest.xml
  - TEST-WellKnownValueSupportTest.xml

[GRADLE_OUTPUT]  (only on failure or with --verbose)
...

=== END_GRADLE_RESULT ===
```

## Test Reports

Report locations after test execution:
- **HTML Report**: `<module>/build/reports/tests/test/index.html`
- **XML Results**: `<module>/build/test-results/test/*.xml` (Recommended)

The `[TEST_REPORTS]` section in output lists all available XML report files.

## Analyzing Failures

### Compilation Errors

The `[COMPILE_ERRORS]` section provides:
```
ERROR_1: /pat/to/workspace/<module>/src/main/kotlin/Foo.kt:42
  Unresolved reference 'NonExistentClass'.
```

Format: `FILE_PATH:LINE_NUMBER` followed by indented error message.

### Test Failures

**Step 1**: Review `[TEST_FAILURES]` section for overview:
```
FAILURE_1:
  CLASS: full.qualifed.SomeClassNameTests
  METHOD: testMethodName()
  MESSAGE: Collection should not contain element 1...
```

**Step 2**: Get XML paths from `[TEST_REPORTS]` section:
```
REPORT_1: ksink/build/test-results/test (2 files)
  - TEST-full.qualifed.SomeClassNameTests.xml
```

**Step 3**: Read the XML file for detailed stack trace:
```xml
<testsuite name="..." tests="2" failures="1" errors="0">
  <testcase name="testMethodName()" classname="full.qualifed.SomeClassNameTests" time="0.042">
    <failure message="..." type="org.opentest4j.AssertionFailedError">
org.opentest4j.AssertionFailedError: Collection should not contain...
	at full.qualifed.SomeClassNameTests.testMethodName(SomeClassNameTests.kt:21)
    </failure>
  </testcase>
</testsuite>
```

**Step 4**: Extract and parse:
- `message` attribute: Full error description
- `type` attribute: Exception class name
- Text content: Stack trace with file:line (e.g., `SomeClassNameTests.kt:21`)

**Step 5**: Report all extracted information to parent agent

## Troubleshooting Options

**Important**: The `-v/--verbose` and `-s/--stacktrace` options are **NOT needed for typical failures** (compilation errors, test assertion failures, etc.). The default output already includes the relevant error information in `[ERROR_ANALYSIS]`.

Use these options **only when Gradle's behavior itself is suspicious**:
- Build hangs or times out unexpectedly
- Cryptic Gradle infrastructure errors
- Task dependency or configuration issues
- Plugin resolution failures

**Configuration Cache Issues**: If you suspect a configuration cache problem (stale cache, serialization errors), retry with:
```bash
scripts/gradle-tool.sh build --no-configuration-cache
scripts/gradle-tool.sh test --no-configuration-cache
```
