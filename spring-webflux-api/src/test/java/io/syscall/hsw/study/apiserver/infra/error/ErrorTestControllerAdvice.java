package io.syscall.hsw.study.apiserver.infra.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorTestControllerAdvice {

    static final String ERROR_HANDLED_BODY = "You did!";

    private static final Logger log = LoggerFactory.getLogger(ErrorTestControllerAdvice.class);

    @ExceptionHandler
    public Mono<ResponseEntity<String>> handleException(CatchMeCheckedException ex, ServerWebExchange exchange) {
        log.info("Got {}", exchange.getRequest().getURI(), ex);
        return Mono.just(ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ERROR_HANDLED_BODY));
    }
}
