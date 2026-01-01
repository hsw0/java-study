package io.syscall.commons.module.appbase.test;

import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@TestPropertySource(properties = {"spring.jmx.enabled=false", "spring.application.admin.enabled=false"})
@ComponentScan(basePackageClasses = AbstractAppBaseWebTest.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractAppBaseWebTest {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected WebTestClient webClient;
}
