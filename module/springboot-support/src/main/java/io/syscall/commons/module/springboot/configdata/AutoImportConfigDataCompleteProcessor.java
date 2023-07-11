package io.syscall.commons.module.springboot.configdata;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.boot.DefaultPropertiesPropertySource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * See {@link ConfigDataEnvironmentPostProcessor}
 */
public final class AutoImportConfigDataCompleteProcessor implements EnvironmentPostProcessor, Ordered {

    public static final int ORDER = AutoImportConfigDataProcessor.ORDER_AFTER_CONFIGDATA;

    private final Log log;

    AutoImportConfigDataCompleteProcessor(DeferredLogFactory logFactory) {
        this.log = logFactory.getLog(getClass());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        var propertySources = environment.getPropertySources();
        if (!(propertySources.get(AutoImportPropertySource.NAME) instanceof AutoImportPropertySource autoImportPs)) {
            log.warn(AutoImportPropertySource.NAME + ": Not found");
            return;
        }

        var autoImportedSources = findAutoImportedSources(propertySources, autoImportPs.getLocations());
        reorder(propertySources, autoImportedSources);
    }

    private void reorder(MutablePropertySources propertySources, List<PropertySource<?>> autoImportedSources) {
        for (var it : autoImportedSources) {
            propertySources.remove(it.getName());
            propertySources.addLast(it);
        }
        DefaultPropertiesPropertySource.moveToEnd(propertySources);
    }

    private List<PropertySource<?>> findAutoImportedSources(
            MutablePropertySources propertySources, List<String> autoImportedLocations) {
        var list = new ArrayList<PropertySource<?>>(autoImportedLocations.size());
        for (var it : propertySources) {
            for (var location : autoImportedLocations) {
                if (it.getName().contains(location)) {
                    list.add(it);
                }
            }
        }
        return list;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
