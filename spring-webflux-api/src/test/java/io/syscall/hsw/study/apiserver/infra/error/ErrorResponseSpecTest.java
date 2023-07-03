package io.syscall.hsw.study.apiserver.infra.error;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.hsw.study.apiserver.infra.ApiInfraLayerTest;
import java.util.function.Consumer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * {@link DefaultExceptionHandler} Response spec Tests
 */
class ErrorResponseSpecTest extends ApiInfraLayerTest {

    @Autowired
    WebTestClient webClient;

    @DisplayName("Handler not found")
    @Test
    void testHandlerNotFound() {
        test(HttpMethod.GET, "/test/route-404/nowhere", result -> {
            assertThat(result.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        });
    }

    @DisplayName("MethodNotAllowedException")
    @Test
    void testMethodNotAllowed() {
        test(HttpMethod.PATCH, ErrorTestController.THROW_CHECKED_PATH, result -> {
            assertThat(result.getStatus().value()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED.value());
        });
    }

    private void test(HttpMethod requestMethod, String uri, Consumer<EntityExchangeResult<ProblemDetail>> validation) {
        webClient.method(requestMethod).uri(uri).exchange().expectAll(responseSpec -> {
            responseSpec.expectStatus().value(Matchers.not(HttpStatus.OK.value()));
            responseSpec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
            responseSpec.expectBody(ProblemDetail.class).consumeWith(result -> {
                var pd = result.getResponseBody();
                assertThat(pd.getStatus())
                        .describedAs("ProblemDetail.status")
                        .isEqualTo(result.getStatus().value());
                validation.accept(result);
            });
        });
    }
}
