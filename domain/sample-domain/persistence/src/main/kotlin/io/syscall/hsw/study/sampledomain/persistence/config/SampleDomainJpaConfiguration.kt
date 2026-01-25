package io.syscall.hsw.study.sampledomain.persistence.config

import io.syscall.commons.module.persistence.jpa.multidatasource.EnableMultiDataSourceJpa
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinition
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinitionBuilder
import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.persistence.repository.PersonRepository
import org.hibernate.cfg.BatchSettings
import org.hibernate.cfg.DialectSpecificSettings
import org.hibernate.cfg.JdbcSettings
import org.hibernate.cfg.MappingSettings
import org.hibernate.cfg.QuerySettings
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
            .jpaProperty(JdbcSettings.DIALECT, "org.hibernate.dialect.H2Dialect")
            .jpaProperty(JdbcSettings.ALLOW_METADATA_ON_BOOT, "false")
            .jpaProperty(JdbcSettings.CONNECTION_PROVIDER_DISABLES_AUTOCOMMIT, "true")
            // DataSourceConnectionProvider에는 적용되지 않음
            // .jpaProperty(JdbcSettings.AUTOCOMMIT, "false")
            .jpaProperty(JdbcSettings.USE_GET_GENERATED_KEYS, "true")
            // .jpaProperty(JdbcSettings.USE_SCROLLABLE_RESULTSET, "???")
            .jpaProperty(DialectSpecificSettings.ORACLE_OSON_DISABLED, "true")
            .jpaProperty(SchemaToolingSettings.HBM2DDL_AUTO, "update")
            .jpaProperty(QuerySettings.IN_CLAUSE_PARAMETER_PADDING, "true")
            .jpaProperty(QuerySettings.FAIL_ON_PAGINATION_OVER_COLLECTION_FETCH, "true")
            .jpaProperty(JdbcSettings.STATEMENT_FETCH_SIZE, "100")
            .jpaProperty(BatchSettings.ORDER_INSERTS, "true")
            .jpaProperty(BatchSettings.ORDER_UPDATES, "true")
            .jpaProperty(BatchSettings.STATEMENT_BATCH_SIZE, "1000")
            .jpaProperty(MappingSettings.KEYWORD_AUTO_QUOTING_ENABLED, "true")
            .jpaProperty(MappingSettings.XML_MAPPING_ENABLED, "false")
            .build()

    @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
        basePackageClasses = [PersonRepository::class],
        entityManagerFactoryRef = "sampleEntityManagerFactory",
        transactionManagerRef = "sampleTransactionManager",
    )
    internal class SampleRepositoryConfiguration
}
