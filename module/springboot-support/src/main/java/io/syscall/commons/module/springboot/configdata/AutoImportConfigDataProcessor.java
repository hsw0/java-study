package io.syscall.commons.module.springboot.configdata;

import org.apache.commons.logging.Log;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.annotation.ImportCandidates;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

/**
 * @see ConfigDataEnvironmentPostProcessor
 */
public final class AutoImportConfigDataProcessor implements EnvironmentPostProcessor, Ordered {

    static final int ORDER_BEFORE_CONFIGDATA = ConfigDataEnvironmentPostProcessor.ORDER - 1;
    static final int ORDER_AFTER_CONFIGDATA = ConfigDataEnvironmentPostProcessor.ORDER + 1;
    public static final int ORDER = ORDER_BEFORE_CONFIGDATA;

    private final Log log;

    AutoImportConfigDataProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getPropertySources().contains(AutoImportPropertySource.NAME)) {
            log.warn(AutoImportPropertySource.NAME + ": Already registered");
            return;
        }

        var autoImportPropertySource = createAutoImportPropertySource(application.getClassLoader());
        environment.getPropertySources().addLast(autoImportPropertySource);
    }

    private PropertySource<?> createAutoImportPropertySource(ClassLoader classLoader) {
        var locations =
                ImportCandidates.load(AutoConfigDataFile.class, classLoader).getCandidates();
        return AutoImportPropertySource.of(locations);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
