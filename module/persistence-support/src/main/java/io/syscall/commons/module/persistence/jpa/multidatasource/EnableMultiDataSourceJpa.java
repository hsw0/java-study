package io.syscall.commons.module.persistence.jpa.multidatasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * Enables multi-datasource JPA configuration.
 *
 * <p>Usage: Apply to a {@code @Configuration} class that provides {@link JpaDataSourceDefinition}
 * beans. Each definition bean will result in the automatic creation of:
 *
 * <ul>
 *   <li>JpaProperties
 *   <li>HibernateProperties
 *   <li>PersistenceManagedTypes
 *   <li>EntityManagerFactory
 *   <li>TransactionManager
 * </ul>
 *
 * <p>Example:
 *
 * <pre>{@code
 * @Configuration
 * @EnableMultiDataSourceJpa
 * public class JpaConfig {
 *
 *     @Bean
 *     DataSource primaryDataSource() {
 *         return DataSourceBuilder.create()
 *             .url("jdbc:h2:mem:primary")
 *             .build();
 *     }
 *
 *     @Bean
 *     JpaDataSourceDefinition primaryJpa() {
 *         return JpaDataSourceDefinitionBuilder.builder()
 *             .name("primary")
 *             .dataSourceBeanName("primaryDataSource")
 *             .entityPackage("com.example.primary")
 *             .primary()
 *             .build();
 *     }
 * }
 * }</pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(MultiDataSourceJpaRegistrar.class)
public @interface EnableMultiDataSourceJpa {}
