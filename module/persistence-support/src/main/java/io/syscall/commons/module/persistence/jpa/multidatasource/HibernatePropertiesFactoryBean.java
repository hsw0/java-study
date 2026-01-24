package io.syscall.commons.module.persistence.jpa.multidatasource;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;

/**
 * FactoryBean that creates {@link HibernateProperties} with the specified configuration.
 *
 * <p>This is AOT-compatible as it uses constructor arguments instead of instance suppliers.
 */
public class HibernatePropertiesFactoryBean implements FactoryBean<HibernateProperties> {

    private final @Nullable String ddlAuto;

    public HibernatePropertiesFactoryBean(@Nullable String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    @Override
    public @Nullable HibernateProperties getObject() {
        var props = new HibernateProperties();
        if (ddlAuto != null) {
            props.setDdlAuto(ddlAuto);
        }
        return props;
    }

    @Override
    public Class<?> getObjectType() {
        return HibernateProperties.class;
    }
}
