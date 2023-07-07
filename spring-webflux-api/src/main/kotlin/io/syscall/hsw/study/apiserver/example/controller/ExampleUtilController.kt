package io.syscall.hsw.study.apiserver.example.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.time.Duration

@RestController
internal class ExampleUtilController {

    @Autowired
    lateinit var virtualThreadPerTaskScheduler: Scheduler

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
        val start = System.nanoTime()
        val buildResponse = fun(): String {
            val elapsed = (System.nanoTime() - start) / 1_000_000
            return "${elapsed}ms elapsed @ ${Thread.currentThread()}"
        }

        return when (strategy ?: SleepStrategy.MONO_DELAY) {
            SleepStrategy.MONO_DELAY -> Mono.delay(Duration.ofMillis(millis))
                .map { buildResponse() }

            SleepStrategy.THREAD_SLEEP_ON_SCHEDULER -> Mono.fromSupplier {
                runCatching { Thread.sleep(millis) }
                buildResponse()
            }.subscribeOn(virtualThreadPerTaskScheduler)

            SleepStrategy.THREAD_SLEEP_CURRENT -> {
                runCatching { Thread.sleep(millis) }
                Mono.just(buildResponse())
            }
        }
    }

    enum class SleepStrategy { MONO_DELAY, THREAD_SLEEP_ON_SCHEDULER, THREAD_SLEEP_CURRENT }
}
