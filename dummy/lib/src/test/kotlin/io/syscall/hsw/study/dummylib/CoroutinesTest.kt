package io.syscall.hsw.study.dummylib

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.test.Test

class CoroutinesTest {

    companion object {
        val log = LoggerFactory.getLogger(javaClass)
    }

    @Test
    fun test1() {
        log.info("Before runBlocking")
        runBlocking {
            log.info("Before launch")
            launch {
                val message = coroutineMethod();
                log.info("Result: {}", message)
            }
            log.info("After launch")
        }
        log.info("After runBlocking")
    }

    suspend fun coroutineMethod(): String {
        log.info("Inside suspend fun")
        delay(Duration.ofMillis(150))
        return "dummy"
    }
}
