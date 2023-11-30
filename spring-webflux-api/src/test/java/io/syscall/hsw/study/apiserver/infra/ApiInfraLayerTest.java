package io.syscall.hsw.study.apiserver.infra;

import io.syscall.hsw.study.apiserver.AbstractApiApplicationTests;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ApiInfraLayerTest extends AbstractApiApplicationTests {

    @SpringBootApplication
    static class Config {}
}
