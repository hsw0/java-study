package io.syscall.commons.module.appbase.test;

import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureWebTestClient
public abstract class AbstractAppBaseTest {}
