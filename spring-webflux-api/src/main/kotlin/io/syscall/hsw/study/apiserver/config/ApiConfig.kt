package io.syscall.hsw.study.apiserver.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
internal class ApiConfig {

    companion object {
        val log = LoggerFactory.getLogger(ApiConfig::class.java)
    }

    @Bean
    fun dummyBean(): String {
        val rootCause = RuntimeException("rootCause")
        val cause = RuntimeException("cause", rootCause)
        val ex = RuntimeException("wtf", cause)
        log.atError().addKeyValue("k1", "v1").log("msg", ex)
        return "hello"
    }
}
