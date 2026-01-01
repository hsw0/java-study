# AGENTS.md

This file provides guidance to AI Code assistant when working with code in this repository.

## Project Overview

Multi-module Gradle project (Kotlin DSL) for studying modern Java/Kotlin development with Spring Boot WebFlux.

- **Languages**: Java 25, Kotlin 2.3
- **Web**: Spring Boot 4.0.1, Spring WebFlux, Project Reactor
- **Data**: Spring Data JPA, Hibernate ORM, H2 (dev)
- **Observability**: Micrometer, OpenTelemetry, Flight Recorder, JMX
- **Build**: Gradle 9.2 with configuration cache, custom convention plugins
- **Quality**: Error Prone, Checker Framework, Spotless/ktlint, JaCoCo
- **Testing**: JUnit 6, AssertJ, Kotest, Reactor Test, BlockHound

## Architecture

### Module Structure

**Reusable Library Modules** (`module/`):
- `annotations` - Custom annotations (Java 17 base)
- `entityid` - Type-safe entity IDs using Kotlin `value class` pattern
  - `entityid:hibernate` - Hibernate integration for entity IDs
- `logging-support` - SLF4J + Logback configuration
- `protocol` - Shared protocol/contract definitions in Kotlin

**Infrastructure Modules** (`module/`):
- `reactor-support` - Reactive infrastructure (Reactor Test, BlockHound, Netty transports)
- `persistence-support` - Data access (Spring Data JPA, Hibernate, transaction management)
- `springboot-support` - Base Spring Boot setup (actuator, metrics, OpenTelemetry, virtual threads)
- `springboot-app-base` - Application-level autoconfiguration (exception handling, health endpoints, Netty/Reactor customization)
- `api-base` - WebFlux API error handling

**Applications**:
- `spring-webflux-api` - Main WebFlux REST API with JPA + H2, demonstrates Person entity CRUD
- `auth-server` - Authentication service (stub)
- `ksink` - Study/learning module for experimentation

**Other**:
- `test-report` - Aggregates test results and coverage from all modules
- `api-spec` - TypeSpec-based OpenAPI 3.1.0 definitions
- `client` - Generated API clients
- `dummy/lib`, `dummy/app` - Example implementations

### Key Patterns

1. **Type-Safe Entity IDs**: Uses Kotlin inline value classes for zero-cost type safety
   ```kotlin
   @JvmInline
   value class PersonId(override val value: Long) : LongEntityId
   ```

2. **Layered Architecture**: Controller → Service → Repository → Entity, with separate protocol/DTO layer

3. **Spring Boot Autoconfiguration**: Module-specific `@AutoConfiguration` classes with conditional activation (SERVLET vs REACTIVE)

4. **Reactive Infrastructure**: WebFlux primary stack with Reactor Scheduler beans, coroutine integration, virtual thread support

5. **Exception Handling**: Centralized via `WebFluxExceptionHandlerSupport`, `PersistenceExceptionHandler` (reactive) and `FallbackServletExceptionHandler` (servlet)

## Gradle Convention Plugins

Located in `gradle/conventions/src/main/kotlin/`. Apply via `id("conventions.X")`:

- `conventions.project.jvm` - Base JVM project setup (logging, testing, dependency management)
- `conventions.project.kotlin` - Kotlin compilation with all-open/no-arg for Spring/JPA
- `conventions.project.java` - Java analysis with Error Prone and Checker Framework
- `conventions.project.spring-boot` - Spring Boot dependencies and annotation processors
- `conventions.project.spring-boot-app` - Spring Boot applications with AOT and reactor-tools
- `conventions.project.spotless` - Code formatting (ktlint)
- `conventions.dependency-management` - BOM management and version constraints

Custom plugins:
- `io.syscall.gradle.plugin.mapstruct` - MapStruct configuration
- `io.syscall.gradle.plugin.devonly` - Development-only dependencies

## Important Configuration

### Dependency Management
- Version catalog: `gradle/libs.versions.toml`
- BOMs composed in `:dependencyManagement` module
- All logging routed to SLF4J (Log4j, JBoss Logging substituted)
- Jakarta transaction-api excluded in favor of Spring's transaction support

### Testing
- Test base classes in `module:springboot-app-base`:
  - `AbstractAppBaseWebTest` - Base for all tests
  - `AbstractWebFluxAppBaseTest` - WebFlux with REACTIVE environment
- BlockHound enabled for detecting blocking calls in reactive code
- JaCoCo aggregation in `:test-report` module
