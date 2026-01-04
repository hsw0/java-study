package io.syscall.commons.module.persistence.jpa.multidatasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.jpa.autoconfigure.JpaProperties;
import org.springframework.orm.jpa.JpaTransactionManager;

/**
 * Support class that registers JPA-related beans for a given datasource configuration.
 *
 * <p>This class programmatically creates and registers:
 *
 * <ul>
 *   <li>JpaProperties - JPA configuration properties
 *   <li>HibernateProperties - Hibernate-specific properties
 *   <li>PersistenceManagedTypes - scanned entity types
 *   <li>HibernateJpaConfiguration - internal configuration bean
 *   <li>LocalContainerEntityManagerFactoryBean - the EntityManagerFactory
 *   <li>JpaTransactionManager - transaction manager for this datasource
 * </ul>
 */
public final class MultiDataSourceJpaConfigSupport {

    private static final Logger log = LoggerFactory.getLogger(MultiDataSourceJpaConfigSupport.class);

    // Spring Boot 4.x's HibernateJpaConfiguration is package-private
    private static final String HIBERNATE_JPA_CONFIG_CLASS =
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaConfiguration";

    private MultiDataSourceJpaConfigSupport() {}

    /**
     * Registers all JPA beans for the given datasource definition.
     *
     * @param registry the bean definition registry
     * @param definition the datasource configuration
     */
    public static void registerJpaBeans(BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        log.info("Registering JPA beans for datasource: {}", definition.name());

        // 1. Register JpaProperties bean
        registerJpaProperties(registry, definition);

        // 2. Register HibernateProperties bean
        registerHibernateProperties(registry, definition);

        // 3. Register PersistenceManagedTypes bean (entity scanning)
        registerPersistenceManagedTypes(registry, definition);

        // 4. Register HibernateJpaConfiguration with custom constructor args
        registerHibernateJpaConfiguration(registry, definition);

        // 5. Register JpaVendorAdapter using factory method
        registerJpaVendorAdapter(registry, definition);

        // 6. Register EntityManagerFactoryBuilder using factory method
        registerEntityManagerFactoryBuilder(registry, definition);

        // 7. Register EntityManagerFactory using factory method
        registerEntityManagerFactory(registry, definition);

        // 8. Register TransactionManager using factory method
        registerTransactionManager(registry, definition);
    }

    /**
     * Registers JPA beans for multiple datasource definitions.
     *
     * @param registry the bean definition registry
     * @param definitions the datasource configurations
     */
    public static void registerJpaBeans(BeanDefinitionRegistry registry, List<JpaDataSourceDefinition> definitions) {
        for (JpaDataSourceDefinition definition : definitions) {
            registerJpaBeans(registry, definition);
        }
    }

    private static void registerJpaProperties(BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();
        bd.setBeanClass(JpaProperties.class);
        bd.setInstanceSupplier(() -> {
            var props = new JpaProperties();
            Map<String, String> properties = new HashMap<>(definition.jpaProperties());
            // Merge hibernate properties into jpa properties
            definition.hibernateProperties().forEach((k, v) -> properties.put("hibernate." + k, v));
            props.setProperties(properties);
            return props;
        });
        bd.setPrimary(definition.primary());
        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registry.registerBeanDefinition(definition.jpaPropertiesBeanName(), bd);
    }

    private static void registerHibernateProperties(
            BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();
        bd.setBeanClass(HibernateProperties.class);
        bd.setInstanceSupplier(() -> {
            var props = new HibernateProperties();
            // Set ddl-auto if provided
            String ddlAuto = definition.hibernateProperties().get("ddl-auto");
            if (ddlAuto != null) {
                props.setDdlAuto(ddlAuto);
            }
            return props;
        });
        bd.setPrimary(definition.primary());
        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registry.registerBeanDefinition(definition.hibernatePropertiesBeanName(), bd);
    }

    private static void registerPersistenceManagedTypes(
            BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();
        bd.setBeanClass(PersistenceManagedTypesFactoryBean.class);

        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addIndexedArgumentValue(0, definition.entityPackages());
        ctorArgs.addIndexedArgumentValue(1, definition.entityClasses());
        bd.setConstructorArgumentValues(ctorArgs);

        bd.setPrimary(definition.primary());
        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        registry.registerBeanDefinition(definition.persistenceManagedTypesBeanName(), bd);
    }

    private static void registerHibernateJpaConfiguration(
            BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        Class<?> hibernateConfigClass;
        try {
            hibernateConfigClass = Class.forName(HIBERNATE_JPA_CONFIG_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "HibernateJpaConfiguration not found. Ensure spring-boot-hibernate is on classpath", e);
        }

        var bd = new GenericBeanDefinition();
        bd.setBeanClass(hibernateConfigClass);
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        // Constructor signature (Spring Boot 4.x):
        // HibernateJpaConfiguration(
        //     DataSource dataSource,                                          // 0
        //     JpaProperties jpaProperties,                                    // 1
        //     ConfigurableListableBeanFactory beanFactory,                    // 2 - autowired
        //     ObjectProvider<JtaTransactionManager> jtaTransactionManager,    // 3 - autowired
        //     HibernateProperties hibernateProperties,                        // 4
        //     ObjectProvider<...> metadataProviders,                          // 5 - autowired
        //     ObjectProvider<SchemaManagementProvider> providers,             // 6 - autowired
        //     ObjectProvider<PhysicalNamingStrategy> physicalNamingStrategy,  // 7 - autowired
        //     ObjectProvider<ImplicitNamingStrategy> implicitNamingStrategy,  // 8 - autowired
        //     ObjectProvider<SQLExceptionTranslator> sqlExceptionTranslator,  // 9 - autowired
        //     ObjectProvider<HibernatePropertiesCustomizer> customizers       // 10 - autowired
        // )

        var ctorArgs = new ConstructorArgumentValues();

        // Index 0: DataSource - reference to the named bean
        ctorArgs.addGenericArgumentValue(
                new RuntimeBeanReference(definition.dataSourceBeanName()), DataSource.class.getName());

        // Index 1: JpaProperties - reference to our created bean
        ctorArgs.addGenericArgumentValue(
                new RuntimeBeanReference(definition.jpaPropertiesBeanName()), JpaProperties.class.getName());

        // Index 4: HibernateProperties - reference to our created bean
        ctorArgs.addGenericArgumentValue(
                new RuntimeBeanReference(definition.hibernatePropertiesBeanName()),
                HibernateProperties.class.getName());

        bd.setConstructorArgumentValues(ctorArgs);
        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        // Mark as not a default candidate to avoid conflicts
        bd.setDefaultCandidate(false);

        registry.registerBeanDefinition(definition.hibernateJpaConfigurationBeanName(), bd);
    }

    private static void registerJpaVendorAdapter(BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();

        // Use factory method on the HibernateJpaConfiguration bean
        bd.setFactoryBeanName(definition.hibernateJpaConfigurationBeanName());
        bd.setFactoryMethodName("jpaVendorAdapter");

        bd.setPrimary(definition.primary());
        bd.setDefaultCandidate(!definition.primary());
        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDependsOn(definition.hibernateJpaConfigurationBeanName());

        registry.registerBeanDefinition(definition.jpaVendorAdapterBeanName(), bd);
    }

    private static void registerEntityManagerFactoryBuilder(
            BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();

        // Use factory method on the HibernateJpaConfiguration bean
        bd.setFactoryBeanName(definition.hibernateJpaConfigurationBeanName());
        bd.setFactoryMethodName("entityManagerFactoryBuilder");
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        // entityManagerFactoryBuilder(JpaVendorAdapter, ObjectProvider, ObjectProvider)
        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.jpaVendorAdapterBeanName()));
        bd.setConstructorArgumentValues(ctorArgs);

        bd.setPrimary(definition.primary());
        bd.setDefaultCandidate(!definition.primary());
        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDependsOn(definition.jpaVendorAdapterBeanName());

        registry.registerBeanDefinition(definition.entityManagerFactoryBuilderBeanName(), bd);
    }

    private static void registerEntityManagerFactory(
            BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();

        // Use factory method on the HibernateJpaConfiguration bean
        bd.setFactoryBeanName(definition.hibernateJpaConfigurationBeanName());
        bd.setFactoryMethodName("entityManagerFactory");
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        // entityManagerFactory(EntityManagerFactoryBuilder, PersistenceManagedTypes)
        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.entityManagerFactoryBuilderBeanName()));
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.persistenceManagedTypesBeanName()));
        bd.setConstructorArgumentValues(ctorArgs);

        bd.setPrimary(definition.primary());
        bd.setDefaultCandidate(!definition.primary());
        bd.setDependsOn(definition.entityManagerFactoryBuilderBeanName(), definition.persistenceManagedTypesBeanName());

        registry.registerBeanDefinition(definition.entityManagerFactoryBeanName(), bd);
    }

    private static void registerTransactionManager(
            BeanDefinitionRegistry registry, JpaDataSourceDefinition definition) {
        var bd = new GenericBeanDefinition();

        // Create JpaTransactionManager directly with explicit EntityManagerFactory reference
        // (factory method approach fails when multiple EMFs exist without a primary)
        bd.setBeanClass(JpaTransactionManager.class);

        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.entityManagerFactoryBeanName()));
        bd.setConstructorArgumentValues(ctorArgs);

        bd.setPrimary(definition.primary());
        bd.setDefaultCandidate(!definition.primary());
        bd.setDependsOn(definition.entityManagerFactoryBeanName());

        registry.registerBeanDefinition(definition.transactionManagerBeanName(), bd);
    }
}
