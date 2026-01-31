package io.syscall.commons.module.jackson.defaults;

import io.syscall.commons.module.jackson.ObjectMapperCustomizer;
import java.util.ArrayList;
import java.util.List;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.EnumFeature;
import tools.jackson.databind.cfg.MapperBuilder;
import tools.jackson.module.kotlin.KotlinFeature;
import tools.jackson.module.kotlin.KotlinModule;

public enum JacksonDefaults implements ObjectMapperCustomizer {
    INSTANCE;

    private static final List<JacksonModule> MODULES = findModules();

    private static List<JacksonModule> findModules() {
        final var cl = JacksonDefaults.class.getClassLoader();

        final var predefinedModules = createPredefinedModules();
        final var foundModules = MapperBuilder.findModules(cl);

        var modules = new ArrayList<>(predefinedModules);

        for (var module : foundModules) {
            for (var pm : predefinedModules) {
                if (pm.getClass() != module.getClass()) {
                    modules.add(module);
                }
            }
        }

        return List.copyOf(modules);
    }

    private static List<JacksonModule> createPredefinedModules() {
        return List.of(createKotlinModule());
    }

    private static KotlinModule createKotlinModule() {
        var kotlinModuleB = new KotlinModule.Builder();
        kotlinModuleB.configure(KotlinFeature.UseJavaDurationConversion, true);
        kotlinModuleB.configure(KotlinFeature.SingletonSupport, true);
        return kotlinModuleB.build();
    }

    @Override
    public <M extends ObjectMapper, B extends MapperBuilder<M, B>> B customize(B builder) {
        builder.addModules(MODULES);

        // default but explicit
        builder.deactivateDefaultTyping();
        builder.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, false);
        builder.configure(DeserializationFeature.FAIL_ON_TRAILING_TOKENS, true);
        builder.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, false);
        builder.configure(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS, false);

        // customizations
        builder.configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true);
        builder.configure(EnumFeature.FAIL_ON_NUMBERS_FOR_ENUMS, true);
        builder.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, false);
        builder.configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false);
        builder.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, true);

        return builder;
    }
}
