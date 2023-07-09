package io.syscall.commons.module.springboot.autoconfigure.disable;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.commons.module.springboot.autoconfigure.disable.cases.Disabled1;
import io.syscall.commons.module.springboot.autoconfigure.disable.cases.Disabled2;
import io.syscall.commons.module.springboot.autoconfigure.disable.cases.Enabled;
import io.syscall.commons.module.springboot.test.TestApplication;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
class ExcludeAutoConfigurationImportFilterTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Order(0)
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        log.info("Application started.");
    }

    @Test
    void test() {
        assertBeanExists(Enabled.class);
        assertBeanNotExists(Disabled1.class);
        assertBeanNotExists(Disabled2.class);

        assertBeanExists(TestApplication.class);
    }

    private <T> void assertBeanExists(Class<T> type) {
        var beans = applicationContext.getBeansOfType(type);
        assertThat(beans.values())
                .describedAs("beans")
                .areAtLeastOne(new Condition<>(type::isInstance, "instance of %s", type));
    }

    private <T> void assertBeanNotExists(Class<T> type) {
        var beans = applicationContext.getBeansOfType(type);
        assertThat(beans.values()).isEmpty();
    }
}
