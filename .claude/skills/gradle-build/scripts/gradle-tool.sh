#!/usr/bin/env bash
# Wrapper script for GradleTool.java
# Uses Gradle Tooling API for build/test execution

set -euo pipefail

readonly SCRIPT_PATH="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Find project root (where gradlew is)
find_project_root() {
    local dir="$PWD"
    while [[ "$dir" != "/" ]]; do
        if [[ -f "$dir/gradlew" ]]; then
            echo "$dir"
            return 0
        fi
        dir="$(dirname "$dir")"
    done
    return 1
}

# Extract Gradle version from gradle-wrapper.properties
get_gradle_version() {
    local project_root="$1"
    local props_file="$project_root/gradle/wrapper/gradle-wrapper.properties"
    if [[ -f "$props_file" ]]; then
        # Extract version from distributionUrl (e.g., gradle-9.3.0-bin.zip -> 9.3.0)
        local url
        url=$(grep -E '^distributionUrl=' "$props_file" | cut -d= -f2-)
        # Decode \: to :
        url="${url//\\:/:/}"
        # Extract version: gradle-X.Y.Z-bin.zip or gradle-X.Y.Z-all.zip
        local version
        version=$(echo "$url" | sed -E 's/.*gradle-([0-9]+\.[0-9]+(\.[0-9]+)?)-[^/]+\.zip$/\1/')
        if [[ -n "$version" ]]; then
            echo "$version"
            return 0
        fi
    fi
    return 1
}

# Find Gradle lib directory
find_gradle_lib() {
    # Strategy 1: GRADLE_HOME environment variable
    if [[ -n "${GRADLE_HOME:-}" ]] && [[ -d "$GRADLE_HOME/lib" ]]; then
        echo "$GRADLE_HOME/lib"
        return 0
    fi

    # Strategy 2: Use version from gradle-wrapper.properties
    local project_root
    project_root=$(find_project_root) || true

    local wrapper_dists="$HOME/.gradle/wrapper/dists"
    if [[ -d "$wrapper_dists" ]]; then
        local target_version=""
        local target_gradle_dir=""

        if [[ -n "$project_root" ]]; then
            target_version=$(get_gradle_version "$project_root") || true
        fi

        if [[ -n "$target_version" ]]; then
            # Look for specific version (try both -bin and -all)
            for suffix in "bin" "all"; do
                local gradle_dir="$wrapper_dists/gradle-${target_version}-${suffix}"
                if [[ -d "$gradle_dir" ]]; then
                    target_gradle_dir="$gradle_dir"
                    break
                fi
            done
        fi

        # Fallback: find latest version
        if [[ -z "$target_gradle_dir" ]]; then
            target_gradle_dir=$(find "$wrapper_dists" -maxdepth 1 -type d -name 'gradle-*-bin' | sort -V | tail -1)
        fi

        if [[ -n "$target_gradle_dir" ]]; then
            local lib_dir
            lib_dir=$(find "$target_gradle_dir" -maxdepth 3 -type d -name 'lib' | head -1)
            if [[ -n "$lib_dir" ]]; then
                echo "$lib_dir"
                return 0
            fi
        fi
    fi

    return 1
}

GRADLE_LIB=$(find_gradle_lib) || {
    echo "[-] Could not find Gradle installation" >&2
    echo "    Set GRADLE_HOME or ensure Gradle wrapper is initialized" >&2
    exit 1
}

# Run GradleTool.java
exec java \
    --enable-native-access=ALL-UNNAMED \
    --class-path "$GRADLE_LIB/*" \
    "$SCRIPT_PATH/GradleTool.java" \
    "$@"
