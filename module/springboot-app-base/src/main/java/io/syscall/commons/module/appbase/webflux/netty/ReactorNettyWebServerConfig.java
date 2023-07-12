package io.syscall.commons.module.appbase.webflux.netty;

import java.util.function.UnaryOperator;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.embedded.netty.NettyServerCustomizer;
import org.springframework.context.annotation.Bean;
import reactor.netty.http.server.HttpServer;
import reactor.netty.http.server.HttpServerMetricsRecorder;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass({NettyServerCustomizer.class})
@AutoConfiguration
public class ReactorNettyWebServerConfig {

    @Bean
    SimpleRequestMappingPatternResolver routeResolver() {
        return new SimpleRequestMappingPatternResolver();
    }

    @Bean
    NettyServerCustomizer enableNettyMetrics(SimpleRequestMappingPatternResolver routeResolver) {
        return httpServer -> {
            var defaultMetricsRecorder = getDefaultMetricsRecorder(httpServer);

            var metricsRecorder = new CustomHttpServerMetricsRecorder(defaultMetricsRecorder, routeResolver);
            return httpServer.metrics(/* enable= */ true, () -> metricsRecorder);
        };
    }

    /**
     * reactor.netty.http.server.MicrometerHttpServerMetricsRecorder 가 package-private 이어서 이를 얻어오려는 삽질
     *
     * @return {@link reactor.netty.http.server.MicrometerHttpServerMetricsRecorder}
     */
    @SuppressWarnings("JavadocReference")
    HttpServerMetricsRecorder getDefaultMetricsRecorder(HttpServer httpServer) {
        UnaryOperator<String> uriTagValue = ignored -> "DUMMY";
        try {
            // A "NullPointerException" could be thrown; "metricsRecorder()" can return null.
            // 아니 NPE를 잡았는데 왜 그러니
            @SuppressWarnings({"dereference.of.nullable", "DataFlowIssue", "java:S2259"})
            var tmp = httpServer
                    .metrics(/* enable= */ true, uriTagValue)
                    .configuration()
                    .metricsRecorder()
                    .get();
            return (HttpServerMetricsRecorder) tmp;
        } catch (NullPointerException e) {
            throw new BeanInitializationException("Failed to get Default HttpServerMetricsRecorder", e);
        }
    }
}
