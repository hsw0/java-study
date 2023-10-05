package io.syscall.commons.module.appbase.servlet.error;

import io.syscall.commons.module.appbase.servlet.AbstractServletAppBaseTest;
import io.syscall.commons.module.appbase.webflux.error.AbstractErrorTestController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * {@link LoopbackErrorController} Tests
 */
class ServletExceptionHandlerTest extends AbstractServletAppBaseTest {

    @Autowired
    LoopbackErrorController loopbackErrorController;

    @DisplayName("No errors")
    @Test
    void testNoException() {
        webClient.get().uri(AbstractErrorTestController.SUCCESS_PATH).exchange().expectAll(spec -> {
            spec.expectStatus().isEqualTo(HttpStatus.ACCEPTED.value());
            spec.expectBody(String.class).isEqualTo("This is fine.");
        });
    }

    @DisplayName("Normal @ExceptionHandler Path")
    @Test
    void testNormalExceptionHandlerPath() {
        webClient
                .post()
                .uri(AbstractErrorTestController.THROW_CHECKED_PATH)
                .exchange()
                .expectAll(spec -> {
                    spec.expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT);
                    spec.expectBody(String.class).isEqualTo(ServletErrorTestControllerAdvice.ERROR_HANDLED_BODY);
                });
    }

    @DisplayName("Servlet Filter + @ExceptionHandler")
    @Test
    void testServletFilterExceptionHandled() {
        webClient.post().uri(ErrorTestFilter.HANDLED_EXCEPTION_PATH).exchange().expectAll(spec -> {
            spec.expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT);
            spec.expectBody(String.class).isEqualTo(ServletErrorTestControllerAdvice.ERROR_HANDLED_BODY);
        });
    }

    @DisplayName("Servlet Filter + NO @ExceptionHandler")
    @Test
    void testFaultyWebFilter() {
        webClient
                .post()
                .uri(ErrorTestFilter.UNHANDLED_EXCEPTION_PATH)
                .exchange()
                .expectAll(spec -> {
                    spec.expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    spec.expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
                });
    }
}
