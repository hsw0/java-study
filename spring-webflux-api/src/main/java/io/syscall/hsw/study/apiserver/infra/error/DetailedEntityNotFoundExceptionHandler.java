package io.syscall.hsw.study.apiserver.infra.error;

import io.syscall.commons.module.apibase.webflux.error.WebFluxExceptionHandlerSupport;
import io.syscall.hsw.study.apiserver.infra.jpa.hibernate.DetailedEntityNotFoundDelegate.DetailedEntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class DetailedEntityNotFoundExceptionHandler extends WebFluxExceptionHandlerSupport {

    @ExceptionHandler
    public Mono<ResponseEntity<Object>> handleEntityNotFoundException(
            DetailedEntityNotFoundException ex, ServerWebExchange exchange) {
        return handleExceptionInternal(ex, null, null, HttpStatus.NOT_FOUND, exchange);
    }
}
