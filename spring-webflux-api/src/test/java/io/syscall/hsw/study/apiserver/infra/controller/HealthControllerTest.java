package io.syscall.hsw.study.apiserver.infra.controller;

import io.syscall.hsw.study.apiserver.infra.ApiInfraLayerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

class HealthControllerTest extends ApiInfraLayerTest {

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
