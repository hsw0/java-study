package io.syscall.hsw.study.sampledomain.persistence.config

import io.syscall.commons.module.persistence.jpa.multidatasource.EnableMultiDataSourceJpa
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinition
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinitionBuilder
import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.persistence.repository.PersonRepository
import org.hibernate.cfg.AvailableSettings
import org.hibernate.cfg.DialectSpecificSettings
import org.hibernate.cfg.SchemaToolingSettings
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import javax.sql.DataSource

@AutoConfiguration
@EnableMultiDataSourceJpa
public class SampleDomainJpaConfiguration {

    @Bean
    public fun sampleJpaDefinition(
        @Qualifier("sample") dataSource: ObjectProvider<DataSource>,
    ): JpaDataSourceDefinition =
        JpaDataSourceDefinitionBuilder
            .builder()
            .name("sample")
            .dataSourceBeanName("sampleDataSource")
            .entityPackage(PersonEntity::class.java.packageName)
            .jpaProperty(DialectSpecificSettings.ORACLE_OSON_DISABLED, "true")
            .jpaProperty(AvailableSettings.HBM2DDL_AUTO, "update")
            .jpaProperty(AvailableSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true")
            .jpaProperty(AvailableSettings.AUTOCOMMIT, "false")
            // .jpaProperty(AvailableSettings.ALLOW_METADATA_ON_BOOT, "false")
            .jpaProperty(AvailableSettings.DIALECT, "org.hibernate.dialect.H2Dialect")
            .jpaProperty(AvailableSettings.USE_GET_GENERATED_KEYS, "true")
            .jpaProperty(AvailableSettings.IN_CLAUSE_PARAMETER_PADDING, "true")
            .jpaProperty(AvailableSettings.KEYWORD_AUTO_QUOTING_ENABLED, "true")
            .jpaProperty(AvailableSettings.FAIL_ON_PAGINATION_OVER_COLLECTION_FETCH, "true")
            .jpaProperty(AvailableSettings.XML_MAPPING_ENABLED, "false")
            .build()

    @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
        basePackageClasses = [PersonRepository::class],
        entityManagerFactoryRef = "sampleEntityManagerFactory",
        transactionManagerRef = "sampleTransactionManager",
    )
    internal class SampleRepositoryConfiguration
}
