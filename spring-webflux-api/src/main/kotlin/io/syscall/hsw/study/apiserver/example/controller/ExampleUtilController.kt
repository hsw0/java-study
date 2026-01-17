package io.syscall.hsw.study.apiserver.example.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.time.Duration
import kotlin.time.TimeSource

@RestController
internal class ExampleUtilController {

    @GetMapping("/util/current-thread")
    internal fun getCurrentThreadName(): String {
        val thread = Thread.currentThread()
        return thread.toString()
    }

    @GetMapping("/util/sleep/{millis}")
    internal fun sleep(
        @PathVariable millis: Long,
        @RequestParam(required = false) strategy: SleepStrategy?,
    ): Mono<String> {
        val startedThread = Thread.currentThread().name
        val start = TimeSource.Monotonic.markNow()
        val buildResponse = fun(): String {
            val elapsed = start.elapsedNow()
            val currentThread = Thread.currentThread().name
            return "$startedThread -> $elapsed elapsed @ $currentThread"
        }

        return when (strategy ?: SleepStrategy.MONO_DELAY) {
            SleepStrategy.MONO_DELAY -> {
                Mono
                    .delay(Duration.ofMillis(millis))
                    .map { buildResponse() }
            }

            SleepStrategy.THREAD_SLEEP_CURRENT -> {
                runCatching { Thread.sleep(millis) }
                Mono.just(buildResponse())
            }
        }
    }

    enum class SleepStrategy { MONO_DELAY, THREAD_SLEEP_CURRENT }
}
