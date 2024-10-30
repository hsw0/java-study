package io.syscall.commons.module.appbase.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ImportAutoConfiguration({
    ConfigurationPropertiesAutoConfiguration.class,
    PropertyPlaceholderAutoConfiguration.class,
    LifecycleAutoConfiguration.class,
    ApplicationAvailabilityAutoConfiguration.class,
})
@ComponentScan
class CommonAppBaseAutoConfiguration {}
