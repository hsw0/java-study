package io.syscall.commons.module.springboot.configdata;

import java.util.List;
import java.util.Map;
import org.springframework.core.env.MapPropertySource;

public final class AutoImportPropertySource extends MapPropertySource {

    /**
     * @see org.springframework.boot.context.config.ConfigDataEnvironment#IMPORT_PROPERTY
     */
    @SuppressWarnings("JavadocReference")
    static final String CONFIG_DATA_IMPORT_PROPERTY = "spring.config.import";

    public static final String NAME = AutoImportPropertySource.class.getSimpleName();

    private final List<String> locations;

    static AutoImportPropertySource of(List<String> locations) {
        return new AutoImportPropertySource(List.copyOf(locations));
    }

    private AutoImportPropertySource(List<String> locations) {
        super(NAME, Map.of(CONFIG_DATA_IMPORT_PROPERTY, locations));
        this.locations = locations;
    }

    List<String> getLocations() {
        return locations;
    }
}
