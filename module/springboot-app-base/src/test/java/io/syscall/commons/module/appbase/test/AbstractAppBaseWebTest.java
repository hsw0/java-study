package io.syscall.commons.module.appbase.test;

import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ComponentScan(basePackageClasses = AbstractAppBaseWebTest.class)
public abstract class AbstractAppBaseWebTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected WebTestClient webClient;
}
