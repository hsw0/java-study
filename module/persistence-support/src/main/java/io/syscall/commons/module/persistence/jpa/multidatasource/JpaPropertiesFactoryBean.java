package io.syscall.commons.module.persistence.jpa.multidatasource;

import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;

/**
 * FactoryBean that creates {@link JpaProperties} with the specified properties.
 *
 * <p>This is AOT-compatible as it uses constructor arguments instead of instance suppliers.
 */
public class JpaPropertiesFactoryBean implements FactoryBean<JpaProperties> {

    private final Map<String, String> jpaProperties;
    private final Map<String, String> hibernateProperties;

    public JpaPropertiesFactoryBean(Map<String, String> jpaProperties, Map<String, String> hibernateProperties) {
        this.jpaProperties = new HashMap<>(jpaProperties);
        this.hibernateProperties = new HashMap<>(hibernateProperties);
    }

    @Override
    public @Nullable JpaProperties getObject() {
        var props = new JpaProperties();
        Map<String, String> properties = new HashMap<>(jpaProperties);
        // Merge hibernate properties into jpa properties with hibernate. prefix
        hibernateProperties.forEach((k, v) -> properties.put("hibernate." + k, v));
        props.setProperties(properties);
        return props;
    }

    @Override
    public Class<?> getObjectType() {
        return JpaProperties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
