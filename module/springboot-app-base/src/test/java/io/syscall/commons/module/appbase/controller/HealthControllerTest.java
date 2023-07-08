package io.syscall.commons.module.appbase.controller;

import io.syscall.commons.module.appbase.test.AbstractAppBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

class HealthControllerTest extends AbstractAppBaseTest {

    @Autowired
    WebTestClient webClient;

    @Test
    void testLivenessProbe() {
        expectUp("/internal/healthz/live");
    }

    @Test
    void testReadinessProbe() {
        expectUp("/internal/healthz/ready");
    }

    @Test
    void testStartupProbe() {
        expectUp("/internal/healthz/startup");
    }

    private void expectUp(String path) {
        webClient.get().uri(path).exchange().expectAll(responseSpec -> {
            responseSpec.expectStatus().isOk();
            responseSpec.expectBody(String.class).isEqualTo("UP");
        });
    }
}
