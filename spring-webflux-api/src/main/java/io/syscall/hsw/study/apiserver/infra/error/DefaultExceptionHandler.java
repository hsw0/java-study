package io.syscall.hsw.study.apiserver.infra.error;

import io.syscall.hsw.study.apiserver.infra.jpa.hibernate.DetailedEntityNotFoundDelegate.DetailedEntityNotFoundException;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @ExceptionHandler
    public Mono<ResponseEntity<Object>> handleEntityNotFoundException(DetailedEntityNotFoundException ex,
            ServerWebExchange exchange) {
        return handleExceptionInternal(ex, null, null, HttpStatus.NOT_FOUND, exchange);
    }

    @Override
    protected Mono<ResponseEntity<Object>> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            @Nullable HttpHeaders headers,
            HttpStatusCode status,
            ServerWebExchange exchange) {
        log(ex, exchange);
        return super.handleExceptionInternal(ex, body, headers, status, exchange);
    }

    private void log(Exception ex, ServerWebExchange exchange) {
        var request = exchange.getRequest();
        var logEvent = log.atError()
                .setCause(ex)
                .setMessage("Exception!")
                .addKeyValue("http.request.method", request.getMethod().name())
                .addKeyValue("url.full", request.getURI());
        if (exchange.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE) instanceof HandlerMethod hm) {
            logEvent = logEvent.addKeyValue("code.namespace", hm.getBeanType().getSimpleName())
                    .addKeyValue("code.function", hm.getMethod().getName());
        }
        logEvent.log();
    }
}
