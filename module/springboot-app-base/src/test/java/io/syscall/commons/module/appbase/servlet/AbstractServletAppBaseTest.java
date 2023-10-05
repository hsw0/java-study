package io.syscall.commons.module.appbase.servlet;

import io.syscall.commons.module.appbase.test.AbstractAppBaseWebTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.main.web-application-type=SERVLET")
@SpringBootTest(classes = TestServletApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractServletAppBaseTest extends AbstractAppBaseWebTest {}
