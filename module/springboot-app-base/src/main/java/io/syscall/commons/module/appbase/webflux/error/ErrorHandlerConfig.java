package io.syscall.commons.module.appbase.webflux.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.reactive.DispatchExceptionHandler;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;

@Configuration(proxyBeanMethods = false)
@AutoConfiguration(before = ErrorWebFluxAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(DispatchExceptionHandler.class)
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
