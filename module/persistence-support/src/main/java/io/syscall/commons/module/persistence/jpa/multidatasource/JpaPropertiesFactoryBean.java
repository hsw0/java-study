package io.syscall.commons.module.persistence.jpa.multidatasource;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

    public JpaPropertiesFactoryBean(Map<String, String> jpaProperties) {
        this.jpaProperties = new LinkedHashMap<>(jpaProperties);
    }

    @Override
    public @Nullable JpaProperties getObject() {
        var props = new JpaProperties();
        Map<String, String> properties = new HashMap<>(jpaProperties);
        props.setProperties(properties);
        return props;
    }

    @Override
    public Class<?> getObjectType() {
        return JpaProperties.class;
    }
}
