package io.syscall.commons.module.persistence.jpa.multidatasource;

import java.util.List;
import java.util.Map;

/**
 * Configuration holder for a single datasource's JPA setup.
 *
 * @param name unique identifier for this datasource (e.g., "primary", "secondary")
 * @param dataSourceBeanName the bean name of the DataSource to use
 * @param entityPackages packages to scan for JPA entities
 * @param entityClasses explicit entity classes (alternative to package scanning)
 * @param jpaProperties additional JPA properties (spring.jpa.properties.*)
 */
public record JpaDataSourceDefinition(
        String name,
        String dataSourceBeanName,
        List<String> entityPackages,
        List<Class<?>> entityClasses,
        Map<String, String> jpaProperties) {

    @SuppressWarnings("RedundantNullCheck") // Validate before defensive copy
    public JpaDataSourceDefinition {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (dataSourceBeanName == null || dataSourceBeanName.isBlank()) {
            throw new IllegalArgumentException("dataSourceBeanName must not be blank");
        }
        entityPackages = List.copyOf(entityPackages);
        entityClasses = List.copyOf(entityClasses);
        jpaProperties = Map.copyOf(jpaProperties);
    }

    /** Bean name for JpaProperties. */
    public String jpaPropertiesBeanName() {
        return name + "JpaProperties";
    }

    /** Bean name for HibernateProperties. */
    public String hibernatePropertiesBeanName() {
        return name + "HibernateProperties";
    }

    /** Bean name for HibernateJpaConfiguration. */
    public String hibernateJpaConfigurationBeanName() {
        return name + "HibernateJpaConfiguration";
    }

    /** Bean name for EntityManagerFactory. */
    public String entityManagerFactoryBeanName() {
        return name + "EntityManagerFactory";
    }

    /** Bean name for TransactionManager. */
    public String transactionManagerBeanName() {
        return name + "TransactionManager";
    }

    /** Bean name for PersistenceManagedTypes. */
    public String persistenceManagedTypesBeanName() {
        return name + "PersistenceManagedTypes";
    }

    /** Bean name for EntityManagerFactoryBuilder. */
    public String entityManagerFactoryBuilderBeanName() {
        return name + "EntityManagerFactoryBuilder";
    }

    /** Bean name for JpaVendorAdapter. */
    public String jpaVendorAdapterBeanName() {
        return name + "JpaVendorAdapter";
    }
}
