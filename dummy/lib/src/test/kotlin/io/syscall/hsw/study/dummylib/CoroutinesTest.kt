package io.syscall.hsw.study.dummylib

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import java.time.Duration
import kotlin.test.Test

private val log = KotlinLogging.logger {}

class CoroutinesTest {

    @Test
    fun test1() {
        log.info { "Before runBlocking" }
        runBlocking {
            log.info { "Before launch" }
            launch {
                val message = coroutineMethod()
                log.info { "Result: $message" }
            }
            log.info { "After launch" }
        }
        log.info { "After runBlocking" }
    }

    suspend fun coroutineMethod(): String {
        log.info { "Inside suspend fun" }
        delay(Duration.ofMillis(150))
        return "dummy"
    }
}
