package io.syscall.commons.module.persistence.jpa.multidatasource;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypesScanner;

/**
 * FactoryBean that creates {@link PersistenceManagedTypes} by scanning specified packages and
 * including explicit entity classes.
 */
public class PersistenceManagedTypesFactoryBean
        implements FactoryBean<PersistenceManagedTypes>, ResourceLoaderAware, InitializingBean {

    private final List<String> entityPackages;
    private final List<Class<?>> entityClasses;
    private @Nullable ResourceLoader resourceLoader;
    private @Nullable PersistenceManagedTypes managedTypes;

    public PersistenceManagedTypesFactoryBean(List<String> entityPackages, List<Class<?>> entityClasses) {
        this.entityPackages = new ArrayList<>(entityPackages);
        this.entityClasses = new ArrayList<>(entityClasses);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() {
        List<String> managedClassNames = new ArrayList<>();
        List<String> managedPackages = new ArrayList<>();

        // Add explicit classes
        for (Class<?> clazz : entityClasses) {
            managedClassNames.add(clazz.getName());
        }

        // Scan packages
        if (!entityPackages.isEmpty() && resourceLoader != null) {
            var scanner = new PersistenceManagedTypesScanner(resourceLoader);
            var scanned = scanner.scan(entityPackages.toArray(String[]::new));
            managedClassNames.addAll(scanned.getManagedClassNames());
            managedPackages.addAll(scanned.getManagedPackages());
        }

        this.managedTypes = PersistenceManagedTypes.of(managedClassNames, managedPackages);
    }

    @Override
    public @Nullable PersistenceManagedTypes getObject() {
        return managedTypes;
    }

    @Override
    public Class<?> getObjectType() {
        return PersistenceManagedTypes.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
