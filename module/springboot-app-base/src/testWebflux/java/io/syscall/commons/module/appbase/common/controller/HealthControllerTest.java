package io.syscall.commons.module.appbase.common.controller;

import io.syscall.commons.module.appbase.webflux.AbstractWebFluxAppBaseTest;
import org.junit.jupiter.api.Test;

class HealthControllerTest extends AbstractWebFluxAppBaseTest {

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
