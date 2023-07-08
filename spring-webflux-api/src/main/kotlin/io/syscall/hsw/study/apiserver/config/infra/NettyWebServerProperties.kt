package io.syscall.hsw.study.apiserver.config.infra

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding
import org.springframework.boot.context.properties.bind.DefaultValue
import org.springframework.validation.annotation.Validated

@Validated
@ConfigurationProperties(prefix = "custom.netty.webserver")
internal data class NettyWebServerProperties
    @ConstructorBinding
    constructor(
        @DefaultValue("false") val accessLog: Boolean,
        @DefaultValue("false") val useVirtualThread: Boolean,
    )
