package io.syscall.commons.module.appbase.servlet.error;

import io.syscall.commons.module.appbase.servlet.AbstractServletAppBaseTest;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

class ServletErrorResponseSpecTest extends AbstractServletAppBaseTest {

    @DisplayName("Handler not found")
    @Test
    void testHandlerNotFound() {
        test(HttpMethod.GET, "/test/route-404/nowhere", result -> {
            Assertions.assertThat(result.getStatus().value()).isEqualTo(HttpStatus.NOT_FOUND.value());
        });
    }

    private void test(HttpMethod requestMethod, String uri, Consumer<EntityExchangeResult<ProblemDetail>> validation) {
        webClient.method(requestMethod).uri(uri).exchange().expectAll(responseSpec -> {
            responseSpec.expectStatus().value(Matchers.not(HttpStatus.OK.value()));
            responseSpec.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
            responseSpec.expectBody(ProblemDetail.class).consumeWith(result -> {
                var pd = result.getResponseBody();
                Assertions.assertThat(pd.getStatus())
                        .describedAs("ProblemDetail.status")
                        .isEqualTo(result.getStatus().value());
                validation.accept(result);
            });
        });
    }
}
