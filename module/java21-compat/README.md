# Java 21 Virtual Thread wrapper

Gradle 빌드 설정 이슈 Workaround
Thread.ofVirtual() 등을 사용하려면 JVM target / release 를 21로 해야 하는데 Kotlin을 같이 사용하면 빌드가 잘 안되는 이슈가 있음

[Multi-Release JAR](https://openjdk.org/jeps/238) 내에서 인터페이스는 Java 17 호환되게 만들고 Virtual thread 생성 경로만 Java 21+ 버전을 구현하여 우회.

더 좋은 방법이 없을려나..?

