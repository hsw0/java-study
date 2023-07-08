package io.syscall.commons.module.appbase;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.commons.module.appbase.test.AbstractAppBaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ApplicationTests extends AbstractAppBaseTest {

    @Autowired
    DummyApplication application;

    @Test
    void contextLoads() {
        assertThat(application).isNotNull();
    }
}
