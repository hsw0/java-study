package io.syscall.hsw.study.apiserver.config.infra

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.syscall.hsw.study.apiserver.infra.reactor.netty.DelegatingLoopResources
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.resources.LoopResources
import java.util.concurrent.Executor
import java.util.concurrent.Executors

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

        val nThreads = Runtime.getRuntime().availableProcessors()
        val threadFactory = Thread.ofVirtual().name("nettyserver-nio-v").factory()
        val executor = Executors.newThreadPerTaskExecutor(threadFactory)

        val loopResources = OverrideServerEventLoopGroup(
            delegate = it.configuration().loopResources(),
            nThreads, executor,
        )
        return@NettyServerCustomizer it.runOn(loopResources)
    }

    internal class OverrideServerEventLoopGroup(
        delegate: LoopResources,
        private val nThreads: Int,
        private val executor: Executor,
    ) : DelegatingLoopResources(delegate) {

        override fun onServer(useNative: Boolean): EventLoopGroup {
            return NioEventLoopGroup(nThreads, executor)
        }
    }
}
