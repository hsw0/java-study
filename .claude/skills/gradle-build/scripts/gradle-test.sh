#!/usr/bin/env bash
# Gradle test wrapper with formatted output and report location guidance
set -euo pipefail

usage() {
    cat <<EOF
Usage: $(basename "$0") [OPTIONS] [TEST_PATTERN]

Gradle test wrapper with clean, formatted output and report paths.

OPTIONS:
    -m, --module MODULE   Target specific module (e.g., :spring-webflux-api)
    -c, --class CLASS     Run specific test class
    -t, --test METHOD     Run specific test method (requires --class)
    -r, --rerun           Rerun tests ignoring cache (--rerun-tasks)
    -v, --verbose         Show full gradle output (default: condensed)
    -s, --stacktrace      Show stacktrace on error
    --no-configuration-cache  Bypass Gradle configuration cache
    -h, --help            Show this help

EXAMPLES:
    $(basename "$0")                                    # Run all tests
    $(basename "$0") -m :spring-webflux-api             # Test specific module
    $(basename "$0") -m :spring-webflux-api -c PersonControllerTest
    $(basename "$0") -m :module:entityid --rerun        # Force rerun tests

NOTE: Output is automatically condensed. Use -v for full output.
EOF
    exit 0
}

MODULE=""
TEST_CLASS=""
TEST_METHOD=""
RERUN=""
VERBOSE=false
STACKTRACE=""
NO_CONFIG_CACHE=""

while [[ $# -gt 0 ]]; do
    case $1 in
        -m|--module)
            MODULE="$2"
            shift 2
            ;;
        -c|--class)
            TEST_CLASS="$2"
            shift 2
            ;;
        -t|--test)
            TEST_METHOD="$2"
            shift 2
            ;;
        -r|--rerun)
            RERUN="--rerun-tasks"
            shift
            ;;
        -v|--verbose)
            VERBOSE=true
            shift
            ;;
        -s|--stacktrace)
            STACKTRACE="--stacktrace"
            shift
            ;;
        --no-configuration-cache)
            NO_CONFIG_CACHE="--no-configuration-cache"
            shift
            ;;
        -h|--help)
            usage
            ;;
        -*)
            echo "Unknown option: $1" >&2
            exit 1
            ;;
        *)
            # Treat as test class if not specified
            if [[ -z "$TEST_CLASS" ]]; then
                TEST_CLASS="$1"
            fi
            shift
            ;;
    esac
done

PROJECT_ROOT=$(pwd)

# Build gradle command
GRADLE_CMD="./gradlew"
if [[ ! -x "$GRADLE_CMD" ]]; then
    echo "[-] gradle command not found" 2>&1
    exit 1
fi

# Build test task
if [[ -n "$MODULE" ]]; then
    TEST_TASK="${MODULE}:test"
else
    TEST_TASK="test"
fi

GRADLE_ARGS=(
    --no-problems-report
    --continue
    --exclude-task detekt
)

GRADLE_ARGS+=("$TEST_TASK")

# Add test filters
if [[ -n "$TEST_CLASS" ]]; then
    if [[ -n "$TEST_METHOD" ]]; then
        GRADLE_ARGS+=("--tests" "${TEST_CLASS}.${TEST_METHOD}")
    else
        GRADLE_ARGS+=("--tests" "*${TEST_CLASS}*")
    fi
fi

[[ -n "$RERUN" ]] && GRADLE_ARGS+=("$RERUN")
[[ -n "$STACKTRACE" ]] && GRADLE_ARGS+=("$STACKTRACE")
[[ -n "$NO_CONFIG_CACHE" ]] && GRADLE_ARGS+=("$NO_CONFIG_CACHE")

echo "=== Gradle Test ==="
echo "Directory: $PROJECT_ROOT"
echo "Command: $GRADLE_CMD ${GRADLE_ARGS[*]}"
echo "==================="

START_TIME=$(date +%s)

# Run gradle and capture output
OUTPUT_FILE=$(mktemp)
EXIT_CODE=0

$GRADLE_CMD "${GRADLE_ARGS[@]}" > "$OUTPUT_FILE" 2>&1 || EXIT_CODE=$?

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

TOTAL_LINES=$(wc -l < "$OUTPUT_FILE" | tr -d ' ')

# Find test result directories (XML reports)
find_test_results() {
    local search_dir="$1"
    find "$search_dir" -path "*/build/test-results/test" -type d 2>/dev/null
}

# Display output based on mode
if [[ "$VERBOSE" == "true" ]]; then
    cat "$OUTPUT_FILE"
else
    echo ""

    if [[ $EXIT_CODE -eq 0 ]]; then
        echo "[Test output: $TOTAL_LINES lines - showing summary only]"
    else
        echo "[Test output: $TOTAL_LINES lines - showing failures]"
        echo ""

        # Show failed tests
        if grep -qE "FAILED$" "$OUTPUT_FILE"; then
            echo "--- Failed Tests ---"
            grep -E "^[A-Za-z].*FAILED$" "$OUTPUT_FILE" | head -20
            echo ""
        fi

        # Show test failure details
        if grep -q "failures" "$OUTPUT_FILE"; then
            echo "--- Failure Summary ---"
            grep -E "[0-9]+ tests? completed, [0-9]+ failed" "$OUTPUT_FILE" | head -5
            echo ""
        fi

        # Show "what went wrong" section
        if grep -q "What went wrong:" "$OUTPUT_FILE"; then
            echo "--- What Went Wrong ---"
            sed -n '/What went wrong:/,/^\* Try:/p' "$OUTPUT_FILE" | head -20
        fi
    fi
fi

echo ""
echo "=== Test Summary ==="
echo "Duration: ${DURATION}s"
echo "Output lines: $TOTAL_LINES"

if [[ $EXIT_CODE -eq 0 ]]; then
    echo "Status: SUCCESS"

    # Show test counts if available
    if grep -q "tests completed" "$OUTPUT_FILE"; then
        grep -E "[0-9]+ tests? completed" "$OUTPUT_FILE" | tail -1
    fi
else
    echo "Status: FAILED (exit code: $EXIT_CODE)"
fi

echo ""
echo "=== Test Reports ==="

# Determine which modules to check
if [[ -n "$MODULE" ]]; then
    MODULE_PATH=$(echo "$MODULE" | sed 's/^://' | tr ':' '/')
    SEARCH_DIRS=("$PROJECT_ROOT/$MODULE_PATH")
else
    SEARCH_DIRS=("$PROJECT_ROOT")
fi

FOUND_REPORTS=0
for dir in "${SEARCH_DIRS[@]}"; do
    if [[ -d "$dir" ]]; then
        while IFS= read -r result_dir; do
            if [[ -n "$result_dir" ]]; then
                MODULE_DIR=$(dirname "$(dirname "$result_dir")")
                MODULE_NAME="${MODULE_DIR#$PROJECT_ROOT/}"
                REL_PATH="${result_dir#$PROJECT_ROOT/}"

                XML_FILES=$(find "$result_dir" -name "*.xml" 2>/dev/null)
                XML_COUNT=$(echo "$XML_FILES" | grep -c . 2>/dev/null || echo "0")

                echo "Module: $MODULE_NAME"
                echo "  Path: $REL_PATH ($XML_COUNT files)"

                # List XML files
                if [[ -n "$XML_FILES" ]]; then
                    echo "$XML_FILES" | while read -r xml_file; do
                        echo "    - $(basename "$xml_file")"
                    done
                fi
                FOUND_REPORTS=$((FOUND_REPORTS + 1))
            fi
        done < <(find_test_results "$dir")
    fi
done

if [[ $FOUND_REPORTS -eq 0 ]]; then
    echo "No test results found."
    echo "Expected location: <module>/build/test-results/test/"
fi

if [[ $EXIT_CODE -ne 0 ]]; then
    echo ""
    echo "=== Next Steps ==="
    echo "1. Read Test results for detailed failure info"
    echo "2. Rerun specific test: -c <TestClass>"
    echo "3. For Gradle issues: --no-configuration-cache to bypass cache"
fi

rm -f "$OUTPUT_FILE"
echo "===================="

exit $EXIT_CODE
