package io.syscall.hsw.study.apiserver.config.infra

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.syscall.hsw.study.apiserver.infra.reactor.netty.DelegatingLoopResources
import io.syscall.hsw.study.java21.thread.ThreadBuilder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.resources.LoopResources

private val log = KotlinLogging.logger {}

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NettyWebServerProperties::class)
internal class WebServerConfig {

    @Bean
    internal fun nettyServerCustomizer(properties: NettyWebServerProperties) = NettyServerCustomizer {
        log.info { "NettyServer: Applying customization. $properties" }
        return@NettyServerCustomizer it
            .accessLog(properties.accessLog)
    }

    @Bean
    internal fun virtualThreadCustomizer(properties: NettyWebServerProperties) = NettyServerCustomizer {
        if (!properties.useVirtualThread) {
            return@NettyServerCustomizer it
        }
        log.info { "NettyServer: Using virtual thread" }

        val currentLoopResources = it.configuration().loopResources()
        return@NettyServerCustomizer it.runOn(OverrideServerEventLoopGroup(currentLoopResources))
    }

    internal class OverrideServerEventLoopGroup(delegate: LoopResources) : DelegatingLoopResources(delegate) {

        override fun onServer(useNative: Boolean): EventLoopGroup {
            val initialThreads: Long = 1
            val nThreads = Runtime.getRuntime().availableProcessors()
            val threadFactory = ThreadBuilder.ofVirtual()
                .name("nettyserver-nio", initialThreads)
                .factory()
            return NioEventLoopGroup(nThreads, threadFactory)
        }
    }
}
