# Gradle 관련

## Debugging

1. IntelliJ 에서 Remote JVM Debug 구성 생성
   - Debugger mode: Listen to remote JVM

```bash
./gradlew --no-daemon -Dorg.gradle.debug=true -Dorg.gradle.debug.server=false test
```

## 참고자료

* ["Herding Elephants: Wrangling a 3,500-module Gradle project", Square](https://developer.squareup.com/blog/herding-elephants/)
* [Example of how to idiomatically structure a large build with Gradle 7.2+](https://github.com/jjohannes/idiomatic-gradle)
* [opentelemetry-java-instrumentation: conventions/](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/v1.27.0/conventions/src/main/kotlin)
