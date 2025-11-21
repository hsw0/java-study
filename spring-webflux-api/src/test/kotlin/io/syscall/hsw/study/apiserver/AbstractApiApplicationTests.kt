package io.syscall.hsw.study.apiserver

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient

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
