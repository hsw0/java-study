package io.syscall.commons.module.jackson.test;

import tools.jackson.core.Version;
import tools.jackson.databind.JacksonModule;

public final class TestServiceModule extends JacksonModule {

    public final String MODULE_NAME = getClass().getName();

    @Override
    public String getModuleName() {
        return MODULE_NAME;
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {}
}
