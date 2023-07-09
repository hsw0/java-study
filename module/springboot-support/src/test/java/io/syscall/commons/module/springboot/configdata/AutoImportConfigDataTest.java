package io.syscall.commons.module.springboot.configdata;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.commons.module.springboot.test.TestApplication;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
class AutoImportConfigDataTest {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    TestApplication application;

    @Autowired
    ConfigurableApplicationContext applicationContext;

    @Order(0)
    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(application).isNotNull();
        log.info("Initialized.");
    }

    @DisplayName("spring.config.import @ application.yml")
    @Test
    void test0() {
        assertPropertyValue("test-data.whoami", "src/test/resources/application.yml");
        assertPropertyValue("test-data.first", "1st");
        assertPropertyValue("test-data.second", "2nd");
        assertPropertyValue("test-data.override-me-case1", "by-2nd");
    }

    @DisplayName("Using AutoConfigDataFile.imports")
    @Test
    void test1() {
        assertPropertyValue("test-data.from-auto-include", "Overridden by 2nd");
        assertPropertyValue("test-data.from-auto-include-1", "auto-include-first");
        assertPropertyValue("test-data.from-auto-include-2", "auto-include-second");
    }

    @DisplayName("Merged result")
    @Test
    void test3() {
        assertPropertyValue("test-data.override-me-case2", "application.yml");
    }

    void assertPropertyValue(String key, @Nullable String expected) {
        var environment = applicationContext.getEnvironment();
        var actual = environment.getProperty(key);
        showDetails(environment, key);
        assertThat(actual).describedAs("Property '" + key + "'").isEqualTo(expected);
    }

    private void showDetails(ConfigurableEnvironment environment, String key) {
        for (var propertySource : ConfigurationPropertySources.get(environment)) {
            var propertyDetail = propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key));
            if (propertyDetail == null) {
                continue;
            }
            var value = propertyDetail.getValue();
            String sourceDesc;
            try {
                sourceDesc = ((PropertySource<?>) propertyDetail.getSource().getUnderlyingSource()).getName();
            } catch (Exception e) {
                sourceDesc = propertyDetail.getSource().toString();
            }
            log.debug("{}={} source: {}", key, value, sourceDesc);
        }
    }
}
