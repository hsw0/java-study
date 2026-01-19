#!/bin/bash

main() {
  set -eu -o pipefail

  local -r SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

  export LANG=C
  export LC_COLLATE=C
  export LC_CTYPE=C.UTF-8

  local -r src_jar_path="$SCRIPT_DIR"/build/libs/spring-webflux-api.jar
  if [[ ! -r "$src_jar_path" ]]; then
    echo "[-] Source spring boot fat jar not found at $src_jar_path"
    return 1
  fi

  # https://docs.oracle.com/en/java/javase/25/docs/specs/man/java.html
  # https://github.com/openjdk/jdk/blob/jdk-25%2B36/src/hotspot/share/runtime/globals.hpp
  # https://github.com/openjdk/jdk/blob/jdk-25%2B3/src/hotspot/os/linux/globals_linux.hpp

  local JVM_OPTS_BASE=(
    -XX:+AlwaysPreTouch
    -XX:+AlwaysActAsServerClassMachine
    #-XX:PerfDataSaveFile="$TMPDIR"/hsperfdata/%p  # not useful for changing path
    -XX:+UseZGC
    -XX:+UseStringDeduplication
    -XX:MinHeapSize=2g
    -XX:MaxHeapSize=2g
    -XX:MetaspaceSize=128m
    -XX:MaxMetaspaceSize=256m

    -Dsun.net.inetaddr.ttl=2
    -Dsun.net.inetaddr.negative.ttl=5
    -Dsun.net.client.defaultConnectTimeout=5
  )

  java "${JVM_OPTS_BASE[@]}" -XX:+PrintFlagsFinal -version 2> /dev/null | grep -F -v ' {default}'

  java -XshowSettings:properties --version > /dev/null
  java -XshowSettings:locale --version 2>&1 > /dev/null | grep -Ev '^[ ]{8}.+'

  local JVM_OPTS_LOGGING=(
    -Xlog:async
    -Xlog:"all=warning:stderr:time,level,tags"
    -Xlog:"arguments,os+cpu=info:stderr:time,level,tags"
  )

  if [[ "$OSTYPE" == *'linux'* ]]; then
    JVM_OPTS_LOGGING+=(-Xlog:"os+container=info:stderr:time,level,tags")
  fi

  export OTEL_TRACES_EXPORTER=console
  export OTEL_LOGS_EXPORTER=none
  export OTEL_METRICS_EXPORTER=none
  local -r otel_javaagent_path=$(ls "$HOME"/.gradle/caches/modules-2/files-2.1/io.opentelemetry.javaagent/opentelemetry-javaagent/2.24.0/*/opentelemetry-javaagent-*.jar)
  local JVM_OPTS_APM=(
    -javaagent:"$otel_javaagent_path"
    -Dotel.javaagent.configuration-file="$SCRIPT_DIR"/src/main/resources/opentelemetry-javaagent.properties
  )

  local JAVA_OPTS=(
    "${JVM_OPTS_BASE[@]}"
    "${JVM_OPTS_LOGGING[@]}"
    "${JVM_OPTS_APM[@]}"

    -Dspring.aot.enabled=true
  )

  exec java "${JAVA_OPTS[@]}" -jar "$SCRIPT_DIR"/build/libs/spring-webflux-api.jar "$@"
}

main "$@"
exit $?

