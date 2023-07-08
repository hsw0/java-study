package io.syscall.commons.module.appbase.webflux.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

/**
 * 어플리케이션에서 오류 처리기를 지정하지 않아도 뭐라도 제공해준다
 */
@ControllerAdvice
@ConditionalOnClass(ResponseEntityExceptionHandler.class)
@ConditionalOnMissingBean(value = ResponseEntityExceptionHandler.class, ignored = FallbackExceptionHandler.class)
public final class FallbackExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(FallbackExceptionHandler.class);

    public FallbackExceptionHandler() {
        log.error("ExceptionHandler not found. Using fallback!");
    }
}
