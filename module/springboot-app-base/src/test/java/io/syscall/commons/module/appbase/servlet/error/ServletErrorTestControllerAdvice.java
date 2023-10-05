package io.syscall.commons.module.appbase.servlet.error;

import io.syscall.commons.module.appbase.webflux.error.CatchMeCheckedException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class ServletErrorTestControllerAdvice {

    public static final String ERROR_HANDLED_BODY = "You did!";

    private static final Logger log = LoggerFactory.getLogger(ServletErrorTestControllerAdvice.class);

    @ExceptionHandler
    public ResponseEntity<String> handleException(CatchMeCheckedException ex, HttpServletRequest request) {
        log.info("Got {}", request.getRequestURL(), ex);
        return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(ERROR_HANDLED_BODY);
    }
}
