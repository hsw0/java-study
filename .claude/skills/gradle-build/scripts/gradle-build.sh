#!/usr/bin/env bash
# Gradle build wrapper with formatted output for AI assistant consumption
set -euo pipefail

# Output limit configuration
MAX_OUTPUT_LINES=${MAX_OUTPUT_LINES:-100}
SHOW_PROGRESS=${SHOW_PROGRESS:-false}

usage() {
    cat <<EOF
Usage: $(basename "$0") [OPTIONS] [TASKS...]

Gradle build wrapper with clean, formatted output.

OPTIONS:
    -m, --module MODULE   Target specific module (e.g., :spring-webflux-api)
    -q, --quiet           Minimal output (errors only)
    -v, --verbose         Show full gradle output (default: condensed)
    -s, --stacktrace      Show stacktrace on error
    --no-configuration-cache  Bypass Gradle configuration cache
    -h, --help            Show this help

TASKS:
    clean classes testClasses   Default: clean and compile all sources
    build                       Full build with tests
    assemble                    Assemble without tests
    classes                     Compile main classes only
    testClasses                 Compile test classes only

EXAMPLES:
    $(basename "$0")                          # Build entire project
    $(basename "$0") -m :spring-webflux-api   # Build specific module
    $(basename "$0") clean build              # Clean then build
    $(basename "$0") assemble                 # Assemble without tests

NOTE: Output is automatically condensed. Use -v for full output.
EOF
    exit 0
}

MODULE=""
QUIET=""
VERBOSE=false
STACKTRACE=""
NO_CONFIG_CACHE=""
TASKS=()

while [[ $# -gt 0 ]]; do
    case $1 in
        -m|--module)
            MODULE="$2"
            shift 2
            ;;
        -q|--quiet)
            QUIET="--quiet"
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
            TASKS+=("$1")
            shift
            ;;
    esac
done

# Default task
if [[ ${#TASKS[@]} -eq 0 ]]; then
    TASKS=("clean" "classes" "testClasses")
fi

# Build gradle command
GRADLE_CMD="./gradlew"
if [[ ! -x "$GRADLE_CMD" ]]; then
    echo "[-] gradle command not found" 2>&1
    exit 1
fi

GRADLE_ARGS=(
  --no-problems-report
  --continue
  --exclude-task detekt
)

if [[ -n "$MODULE" ]]; then
    for task in "${TASKS[@]}"; do
        GRADLE_ARGS+=("${MODULE}:${task}")
    done
else
    GRADLE_ARGS+=("${TASKS[@]}")
fi

[[ -n "$QUIET" ]] && GRADLE_ARGS+=("$QUIET")
[[ -n "$STACKTRACE" ]] && GRADLE_ARGS+=("$STACKTRACE")
[[ -n "$NO_CONFIG_CACHE" ]] && GRADLE_ARGS+=("$NO_CONFIG_CACHE")

echo "=== Gradle Build ==="
echo "Directory: $(pwd)"
echo "Command: $GRADLE_CMD ${GRADLE_ARGS[*]}"
echo "===================="

START_TIME=$(date +%s)

# Run gradle and capture output
OUTPUT_FILE=$(mktemp)
EXIT_CODE=0

$GRADLE_CMD "${GRADLE_ARGS[@]}" > "$OUTPUT_FILE" 2>&1 || EXIT_CODE=$?

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

TOTAL_LINES=$(wc -l < "$OUTPUT_FILE" | tr -d ' ')

# Display output based on mode
if [[ "$VERBOSE" == "true" ]]; then
    cat "$OUTPUT_FILE"
else
    # Condensed mode: show key information only
    echo ""

    if [[ $EXIT_CODE -eq 0 ]]; then
        # Success: show minimal output
        echo "[Build output: $TOTAL_LINES lines - showing summary only]"
    else
        # Failure: show errors and context
        echo "[Build output: $TOTAL_LINES lines - showing errors]"
        echo ""

        # Show compilation errors
        if grep -q "error:" "$OUTPUT_FILE"; then
            echo "--- Compilation Errors ---"
            grep -E "(\.java|\.kt):[0-9]+:" "$OUTPUT_FILE" | head -30
            echo ""
        fi

        # Show FAILURE block
        if grep -q "FAILURE:" "$OUTPUT_FILE"; then
            echo "--- Failure Details ---"
            grep -A 10 "FAILURE:" "$OUTPUT_FILE" | head -15
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
echo "=== Build Summary ==="
echo "Duration: ${DURATION}s"
echo "Output lines: $TOTAL_LINES"

if [[ $EXIT_CODE -eq 0 ]]; then
    echo "Status: SUCCESS"

    # Extract actionable tasks count
    if grep -q "actionable task" "$OUTPUT_FILE"; then
        grep "actionable task" "$OUTPUT_FILE" | tail -1
    fi
else
    echo "Status: FAILED (exit code: $EXIT_CODE)"
    echo ""
    echo "Tip: For Gradle issues, try --no-configuration-cache"
fi

rm -f "$OUTPUT_FILE"
echo "====================="

exit $EXIT_CODE
