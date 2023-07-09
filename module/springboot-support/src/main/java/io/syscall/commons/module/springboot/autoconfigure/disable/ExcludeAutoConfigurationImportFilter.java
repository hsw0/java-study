package io.syscall.commons.module.springboot.autoconfigure.disable;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 여러 곳에서 <code>spring.autoconfigure.exclude</code> 지정을 허용하는 {@link org.springframework.boot.autoconfigure.AutoConfiguration @AutoConfiguration} 필터
 */
public class ExcludeAutoConfigurationImportFilter implements AutoConfigurationImportFilter, EnvironmentAware {

    private static final Logger log = LoggerFactory.getLogger(ExcludeAutoConfigurationImportFilter.class);

    /**
     * 이거 왜 private이니?
     *
     * @see org.springframework.boot.autoconfigure.AutoConfigurationImportSelector#PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE
     */
    @SuppressWarnings("JavadocReference")
    private static final String PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE = "spring.autoconfigure.exclude";

    private Set<String> exclusions = Set.of();

    @Override
    public boolean[] match(String[] autoConfigurationClasses, AutoConfigurationMetadata autoConfigurationMetadata) {
        boolean[] matches = new boolean[autoConfigurationClasses.length];
        for (int i = 0; i < autoConfigurationClasses.length; i++) {
            var autoConfigurationClass = autoConfigurationClasses[i];
            if (autoConfigurationClass == null) {
                continue;
            }
            var shouldExclude = shouldExclude(autoConfigurationClass);
            matches[i] = !shouldExclude;
            if (shouldExclude) {
                log.trace("Excluded @AutoConfiguration class: {}", autoConfigurationClass);
            }
        }
        return matches;
    }

    private boolean shouldExclude(String name) {
        return exclusions.contains(name);
    }

    /**
     * {@link AutoConfigurationImportSelector#getExcludeAutoConfigurationsProperty()} 와 같으나 중복 선언되더라도 모든 값을 머지
     */
    @SuppressWarnings("JavadocReference")
    private Set<String> getAllExcludeAutoConfigurationsProperty(Environment environment) {
        var allPropertySources = ConfigurationPropertySources.get(environment);

        Set<String> candidates = new LinkedHashSet<>();
        for (var ps : allPropertySources) {
            var binder = new Binder(ps);
            var entries = binder.bind(PROPERTY_NAME_AUTOCONFIGURE_EXCLUDE, String[].class)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());
            candidates.addAll(entries);
        }
        return candidates;
    }

    @Override
    public void setEnvironment(Environment environment) {
        exclusions = getAllExcludeAutoConfigurationsProperty(environment);
    }
}
