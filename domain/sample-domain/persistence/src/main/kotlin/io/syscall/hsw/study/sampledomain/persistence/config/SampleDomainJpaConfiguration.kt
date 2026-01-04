package io.syscall.hsw.study.sampledomain.persistence.config

import io.syscall.commons.module.persistence.jpa.multidatasource.EnableMultiDataSourceJpa
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinition
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinitionBuilder
import io.syscall.hsw.study.sampledomain.model.PersonEntity
import io.syscall.hsw.study.sampledomain.persistence.repository.PersonRepository
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@AutoConfiguration
@EnableMultiDataSourceJpa
public class SampleDomainJpaConfiguration {

    @Bean
    public fun sampleJpaDefinition(): JpaDataSourceDefinition =
        JpaDataSourceDefinitionBuilder
            .builder()
            .name("sample")
            .dataSourceBeanName("sampleDataSource")
            .entityPackage(PersonEntity::class.java.packageName)
            .jpaProperty("hibernate.hbm2ddl.auto", "update")
            .build()

    @Configuration(proxyBeanMethods = false)
    @EnableJpaRepositories(
        basePackageClasses = [PersonRepository::class],
        entityManagerFactoryRef = "sampleEntityManagerFactory",
        transactionManagerRef = "sampleTransactionManager",
    )
    internal class SampleRepositoryConfiguration
}
