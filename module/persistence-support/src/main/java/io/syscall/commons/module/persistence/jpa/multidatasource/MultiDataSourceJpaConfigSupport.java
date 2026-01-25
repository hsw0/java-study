package io.syscall.commons.module.persistence.jpa.multidatasource;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.hibernate.autoconfigure.HibernateProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
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
class MultiDataSourceJpaConfigSupport {

    private static final Logger log = LoggerFactory.getLogger(MultiDataSourceJpaConfigSupport.class);

    // Spring Boot 4.x's HibernateJpaConfiguration is package-private
    private static final String HIBERNATE_JPA_CONFIG_CLASS =
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaConfiguration";

    private final Class<?> hibernateConfigClass;
    private final BeanDefinitionRegistry registry;

    public MultiDataSourceJpaConfigSupport(BeanDefinitionRegistry registry) {
        try {
            hibernateConfigClass = Class.forName(HIBERNATE_JPA_CONFIG_CLASS);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                    "HibernateJpaConfiguration not found. Ensure spring-boot-hibernate is on classpath", e);
        }
        this.registry = registry;
    }

    /**
     * Registers all JPA beans for the given datasource definition.
     *
     * @param definition the datasource configuration
     */
    void registerJpaBeans(JpaDataSourceDefinition definition) {
        log.info("Registering JPA beans for datasource: {}", definition.getName());

        // 1. Register JpaProperties bean
        registerJpaProperties(definition);

        // 2. Register HibernateProperties bean
        registerHibernateProperties(definition);

        // 3. Register PersistenceManagedTypes bean (entity scanning)
        registerPersistenceManagedTypes(definition);

        // 4. Register HibernateJpaConfiguration with custom constructor args
        registerHibernateJpaConfiguration(definition);

        // 5. Register JpaVendorAdapter using factory method
        registerJpaVendorAdapter(definition);

        // 6. Register EntityManagerFactoryBuilder using factory method
        registerEntityManagerFactoryBuilder(definition);

        // 7. Register EntityManagerFactory using factory method
        registerEntityManagerFactory(definition);

        // 8. Register TransactionManager using factory method
        registerTransactionManager(definition);
    }

    private void registerJpaProperties(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition(JpaPropertiesFactoryBean.class);

        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDefaultCandidate(false);

        // Use FactoryBean with constructor args for AOT compatibility
        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addIndexedArgumentValue(0, definition.getJpaProperties());
        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getJpaPropertiesBeanName(), bd);
    }

    private void registerHibernateProperties(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition(HibernatePropertiesFactoryBean.class);

        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDefaultCandidate(false);

        // Use FactoryBean with constructor args for AOT compatibility
        var ctorArgs = new ConstructorArgumentValues();
        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getHibernatePropertiesBeanName(), bd);
    }

    private void registerPersistenceManagedTypes(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition(PersistenceManagedTypesFactoryBean.class);

        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDefaultCandidate(false);

        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addIndexedArgumentValue(0, definition.getEntityPackages());
        ctorArgs.addIndexedArgumentValue(1, definition.getEntityClasses());
        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getPersistenceManagedTypesBeanName(), bd);
    }

    private void registerHibernateJpaConfiguration(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition(hibernateConfigClass);

        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);

        // Mark as not a default candidate to avoid conflicts
        bd.setDefaultCandidate(false);

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
                new RuntimeBeanReference(definition.getDataSourceBeanName()), DataSource.class.getName());

        // Index 1: JpaProperties - reference to our created bean
        ctorArgs.addGenericArgumentValue(
                new RuntimeBeanReference(definition.getJpaPropertiesBeanName()), JpaProperties.class.getName());

        // Index 4: HibernateProperties - reference to our created bean
        ctorArgs.addGenericArgumentValue(
                new RuntimeBeanReference(definition.getHibernatePropertiesBeanName()),
                HibernateProperties.class.getName());

        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getHibernateJpaConfigurationBeanName(), bd);
    }

    private void registerJpaVendorAdapter(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition();

        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDefaultCandidate(false);

        // Use factory method on the HibernateJpaConfiguration bean
        bd.setFactoryBeanName(definition.getHibernateJpaConfigurationBeanName());
        bd.setFactoryMethodName("jpaVendorAdapter");

        registry.registerBeanDefinition(definition.getJpaVendorAdapterBeanName(), bd);
    }

    private void registerEntityManagerFactoryBuilder(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition(EntityManagerFactoryBuilder.class);

        bd.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        bd.setDefaultCandidate(false);

        // Use factory method on the HibernateJpaConfiguration bean
        bd.setFactoryBeanName(definition.getHibernateJpaConfigurationBeanName());
        bd.setFactoryMethodName("entityManagerFactoryBuilder");
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        // entityManagerFactoryBuilder(JpaVendorAdapter, ObjectProvider, ObjectProvider)
        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.getJpaVendorAdapterBeanName()));
        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getEntityManagerFactoryBuilderBeanName(), bd);
    }

    private void registerEntityManagerFactory(JpaDataSourceDefinition definition) {
        var bd = new RootBeanDefinition();

        bd.setDependsOn(
                definition.getEntityManagerFactoryBuilderBeanName(), definition.getPersistenceManagedTypesBeanName());
        bd.setDefaultCandidate(false);

        // Use factory method on the HibernateJpaConfiguration bean
        bd.setFactoryBeanName(definition.getHibernateJpaConfigurationBeanName());
        bd.setFactoryMethodName("entityManagerFactory");
        bd.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);

        // entityManagerFactory(EntityManagerFactoryBuilder, PersistenceManagedTypes)
        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.getEntityManagerFactoryBuilderBeanName()));
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.getPersistenceManagedTypesBeanName()));
        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getEntityManagerFactoryBeanName(), bd);
    }

    private void registerTransactionManager(JpaDataSourceDefinition definition) {
        // Create JpaTransactionManager directly with explicit EntityManagerFactory reference
        // (factory method approach fails when multiple EMFs exist without a primary)
        var bd = new RootBeanDefinition(JpaTransactionManager.class);

        bd.setDependsOn(definition.getEntityManagerFactoryBeanName());
        bd.setDefaultCandidate(false);

        var ctorArgs = new ConstructorArgumentValues();
        ctorArgs.addGenericArgumentValue(new RuntimeBeanReference(definition.getEntityManagerFactoryBeanName()));
        bd.setConstructorArgumentValues(ctorArgs);

        registry.registerBeanDefinition(definition.getTransactionManagerBeanName(), bd);
    }
}
