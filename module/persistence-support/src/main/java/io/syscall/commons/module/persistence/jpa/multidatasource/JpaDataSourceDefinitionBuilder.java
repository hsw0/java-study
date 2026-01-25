package io.syscall.commons.module.persistence.jpa.multidatasource;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * Fluent builder for {@link JpaDataSourceDefinition}.
 */
public final class JpaDataSourceDefinitionBuilder {

    private @Nullable String name;
    private @Nullable String dataSourceBeanName;
    private final List<String> entityPackages = new ArrayList<>();
    private final List<Class<?>> entityClasses = new ArrayList<>();
    private final Map<String, String> jpaProperties = new HashMap<>();

    private JpaDataSourceDefinitionBuilder() {}

    public static JpaDataSourceDefinitionBuilder builder() {
        return new JpaDataSourceDefinitionBuilder();
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder name(String name) {
        this.name = name;
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder dataSourceBeanName(String beanName) {
        this.dataSourceBeanName = beanName;
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder entityPackage(String packageName) {
        this.entityPackages.add(packageName);
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder entityPackages(String... packages) {
        this.entityPackages.addAll(List.of(packages));
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder entityPackages(List<String> packages) {
        this.entityPackages.addAll(packages);
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder entityClass(Class<?> clazz) {
        this.entityClasses.add(clazz);
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder entityClasses(Class<?>... classes) {
        this.entityClasses.addAll(List.of(classes));
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder entityClasses(List<Class<?>> classes) {
        this.entityClasses.addAll(classes);
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder jpaProperty(String key, String value) {
        this.jpaProperties.put(key, value);
        return this;
    }

    @CanIgnoreReturnValue
    public JpaDataSourceDefinitionBuilder jpaProperties(Map<String, String> properties) {
        this.jpaProperties.putAll(properties);
        return this;
    }

    public JpaDataSourceDefinition build() {
        if (name == null) {
            throw new IllegalStateException("name is required");
        }
        if (dataSourceBeanName == null) {
            throw new IllegalStateException("dataSourceBeanName is required");
        }

        return new JpaDataSourceDefinition(name, dataSourceBeanName, entityPackages, entityClasses, jpaProperties);
    }
}
