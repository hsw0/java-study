#!/bin/bash

main() {
  set -eu -o pipefail

  local -r SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

  local -r otel_javaagent_path=$(ls "$HOME"/.gradle/caches/modules-2/files-2.1/io.opentelemetry.javaagent/opentelemetry-javaagent/2.24.0/*/opentelemetry-javaagent-*.jar)

  mkdir -p "$TMPDIR"/hsperfdata

  # https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html
  # https://github.com/openjdk/jdk/blob/jdk-25%2B36/src/hotspot/share/runtime/globals.hpp
  # https://github.com/openjdk/jdk/blob/jdk-25%2B3/src/hotspot/os/linux/globals_linux.hpp

  local JAVA_OPTS=(
     -XX:+AlwaysPreTouch
     -XX:+AlwaysActAsServerClassMachine
     #-XX:PerfDataSaveFile="$TMPDIR"/hsperfdata/%p  # not useful for changing path
     -XX:+UseZGC
     -XX:+UseStringDeduplication
     -XX:MinHeapSize=2g
     -XX:MaxHeapSize=2g
     -XX:MetaspaceSize=128m
     -XX:MaxMetaspaceSize=256m

     -javaagent:"$otel_javaagent_path"
     -Dotel.javaagent.configuration-file="$SCRIPT_DIR"/src/main/resources/opentelemetry-javaagent.properties
     -Dotel.traces.exporter=console
     -Dotel.metrics.exporter=none
     -Dotel.logs.exporter=none

     -Dspring.aot.enabled=true
  )

  (set -x; exec java  "${JAVA_OPTS[@]}" -jar "$SCRIPT_DIR"/build/libs/spring-webflux-api.jar)
}

main "$@"
exit $?

