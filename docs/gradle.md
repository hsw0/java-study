# Gradle 관련

## Debugging

1. IntelliJ 에서 Remote JVM Debug 구성 생성
   - Debugger mode: Listen to remote JVM

```bash
./gradlew --no-daemon -Dorg.gradle.debug=true -Dorg.gradle.debug.server=false test
```
