package io.syscall.commons.module.appbase.servlet.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 어플리케이션에서 오류 처리기를 지정하지 않아도 뭐라도 제공해준다
 */
@ControllerAdvice
@ConditionalOnMissingBean(value = ResponseEntityExceptionHandler.class, ignored = FallbackServletExceptionHandler.class)
public final class FallbackServletExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(FallbackServletExceptionHandler.class);

    public FallbackServletExceptionHandler() {
        log.error("ExceptionHandler not found. Using fallback!");
    }
}
