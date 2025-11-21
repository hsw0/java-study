package io.syscall.commons.module.appbase.webflux.error;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.webflux.autoconfigure.error.ErrorWebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import tools.jackson.databind.ObjectMapper;

@Configuration(proxyBeanMethods = false)
@AutoConfiguration(before = ErrorWebFluxAutoConfiguration.class)
public class ErrorHandlerConfig {

    @Bean
    @Order(-2) // ErrorWebFluxAutoConfiguration.errorWebExceptionHandler : -1
    LoopbackErrorWebExceptionHandler loopbackErrorWebExceptionHandler(
            RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            ObjectProvider<HandlerResultHandler> handlerResultHandlers,
            ObjectMapper objectMapper) {
        return new LoopbackErrorWebExceptionHandler(requestMappingHandlerAdapter, handlerResultHandlers, objectMapper);
    }
}
