package io.syscall.commons.module.appbase.webflux;

import io.syscall.commons.module.appbase.test.AbstractAppBaseWebTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

// testRuntimeClasspath 에 Servlet + Reactive가 있으면 자동으로 Servlet이 선택됨
@TestPropertySource(properties = "spring.main.web-application-type=REACTIVE")
@SpringBootTest(classes = TestWebFluxApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AbstractWebFluxAppBaseTest extends AbstractAppBaseWebTest {}
