package io.syscall.hsw.study.apiserver.config.infra

import io.github.oshai.kotlinlogging.KotlinLogging
import io.netty.channel.EventLoopGroup
import io.netty.channel.nio.NioEventLoopGroup
import io.syscall.hsw.study.apiserver.infra.reactor.netty.CustomHttpServerMetricsRecorder
import io.syscall.hsw.study.apiserver.infra.reactor.netty.DelegatingLoopResources
import io.syscall.hsw.study.apiserver.infra.reactor.netty.SimpleRequestMappingPatternResolver
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.netty.http.server.HttpServerMetricsRecorder
import reactor.netty.resources.LoopResources
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier

private val log = KotlinLogging.logger {}

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(NettyWebServerProperties::class)
internal class WebServerConfig {

    @Bean
    internal fun nettyServerCustomizer(properties: NettyWebServerProperties) = NettyServerCustomizer {
        log.info { "NettyServer: Applying customization. $properties" }
        it.configuration()

        return@NettyServerCustomizer it
            .accessLog(properties.accessLog)
    }

    @Bean
    internal fun enableNettyMetrics(simpleRequestMappingPatternResolver: SimpleRequestMappingPatternResolver) =
        NettyServerCustomizer {

            // reactor.netty.http.server.MicrometerHttpServerMetricsRecorder 가 package-private 이어서 이를 얻어오려는 삽질
            val defaultMetricsRecorder = it
                .metrics(true, { _ -> "DUMMY" })
                .configuration().metricsRecorder()?.get() as HttpServerMetricsRecorder

            val metricsRecorder = CustomHttpServerMetricsRecorder(
                defaultMetricsRecorder,
                simpleRequestMappingPatternResolver,
            )
            return@NettyServerCustomizer it.metrics(true, Supplier { metricsRecorder })
        }

    @Bean
    internal fun virtualThreadCustomizer(properties: NettyWebServerProperties) = NettyServerCustomizer {
        if (!properties.useVirtualThread) {
            return@NettyServerCustomizer it
        }
        log.info { "NettyServer: Using virtual thread" }

        val nThreads = LoopResources.DEFAULT_IO_WORKER_COUNT
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
