package io.syscall.hsw.study.apiserver.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

@Configuration(proxyBeanMethods = false)
@Import(DefaultExceptionHandler.class)
public class ErrorHandlerConfig {

    @Bean
    @Order(-2) // ErrorWebFluxAutoConfiguration.errorWebExceptionHandler : -1
    LoopbackErrorWebExceptionHandler errorWebExceptionHandler(
            RequestMappingHandlerAdapter requestMappingHandlerAdapter,
            ObjectProvider<HandlerResultHandler> handlerResultHandlers,
            ObjectMapper objectMapper) {
        return new LoopbackErrorWebExceptionHandler(requestMappingHandlerAdapter, handlerResultHandlers, objectMapper);
    }
}
