package io.syscall.hsw.study.apiserver

import io.syscall.hsw.study.sampledomain.business.service.PersonService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.context.bean.override.mockito.MockitoBean

@SpringBootTest(classes = [ApiApplication::class])
@MockitoBean(types = [PersonService::class])
@AutoConfigureWebTestClient
abstract class AbstractApiApplicationTests {

    class ContextTest : AbstractApiApplicationTests() {

        @Test
        fun contextLoads() {
            assertTrue(true)
        }
    }
}
