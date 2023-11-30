package io.syscall.hsw.study.apiserver

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [ApiApplication::class])
@AutoConfigureWebTestClient
abstract class AbstractApiApplicationTests {

    class ContextTest : AbstractApiApplicationTests() {

        @Test
        fun contextLoads() {
            assertTrue(true)
        }
    }
}
