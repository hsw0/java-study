package io.syscall.commons.module.appbase.webflux.error;

import io.syscall.commons.module.appbase.test.AbstractAppBaseTest;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * {@link LoopbackErrorWebExceptionHandler} Tests
 */
class WebExceptionHandlerTest extends AbstractAppBaseTest {

    @Autowired
    WebTestClient webClient;

    @Autowired
    LoopbackErrorWebExceptionHandler loopbackErrorWebExceptionHandler;

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
                    spec.expectBody(String.class).isEqualTo(ErrorTestControllerAdvice.ERROR_HANDLED_BODY);
                });
    }

    @DisplayName("WebFilter + @ExceptionHandler")
    @Test
    void testWebFilterExceptionHandled() {
        webClient
                .post()
                .uri(ErrorTestWebFilter.HANDLED_EXCEPTION_PATH)
                .exchange()
                .expectAll(spec -> {
                    spec.expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT);
                    spec.expectBody(String.class).isEqualTo(ErrorTestControllerAdvice.ERROR_HANDLED_BODY);
                });
    }

    @DisplayName("WebFilter + NO @ExceptionHandler")
    @Test
    void testFaultyWebFilter() {
        webClient
                .post()
                .uri(ErrorTestWebFilter.UNHANDLED_EXCEPTION_PATH)
                .exchange()
                .expectAll(spec -> {
                    var expectedResponse = loopbackErrorWebExceptionHandler.buildInternalError();
                    spec.expectStatus().isEqualTo(expectedResponse.statusCode());
                    spec.expectHeader()
                            .contentType(expectedResponse.responseHeaders().get(HttpHeaders.CONTENT_TYPE));
                    spec.expectBody(ByteBuffer.class).isEqualTo(expectedResponse.responseBody());
                });
    }
}
