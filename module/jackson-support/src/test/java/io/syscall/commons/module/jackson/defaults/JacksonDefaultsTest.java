package io.syscall.commons.module.jackson.defaults;

import static org.assertj.core.api.Assertions.assertThat;

import io.syscall.commons.module.jackson.ObjectMapperCustomizer;
import io.syscall.commons.module.jackson.test.TestServiceModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import tools.jackson.databind.json.JsonMapper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JacksonDefaultsTest {

    final ObjectMapperCustomizer sut = JacksonDefaults.INSTANCE;
    JsonMapper.Builder defaultMapperBuilder;
    JsonMapper.Builder customizedMapperBuilder;

    @BeforeEach
    void setUp() {
        defaultMapperBuilder = JsonMapper.builder();
        customizedMapperBuilder = sut.customize(JsonMapper.builder());
    }

    @Test
    void testModuleRegistration() {
        var defaultModules = defaultMapperBuilder.build().registeredModules();
        var customModules = customizedMapperBuilder.build().registeredModules();

        assertThat(customModules)
                .as("customModules")
                .isNotEqualTo(defaultModules)
                .hasAtLeastOneElementOfType(TestServiceModule.class);
    }

    @Test
    void testCustomizations() {}
}
