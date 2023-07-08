package io.syscall.commons.module.appbase.webflux.error;

/**
 * Not handled by {@link org.springframework.web.bind.annotation.ExceptionHandler @ExceptionHandler}
 */
public class CatchMeUnhandledError extends Error {

    public CatchMeUnhandledError() {
        super("Did you?");
    }
}
