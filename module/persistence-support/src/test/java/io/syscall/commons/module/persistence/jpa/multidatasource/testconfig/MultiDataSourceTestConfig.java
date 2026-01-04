package io.syscall.commons.module.persistence.jpa.multidatasource.testconfig;

import io.syscall.commons.module.persistence.jpa.multidatasource.EnableMultiDataSourceJpa;
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinition;
import io.syscall.commons.module.persistence.jpa.multidatasource.JpaDataSourceDefinitionBuilder;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.car.CarEntity;
import io.syscall.commons.module.persistence.jpa.multidatasource.testentities.orange.OrangeEntity;
import javax.sql.DataSource;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Test configuration for multi-datasource JPA tests. */
@SpringBootApplication(
        excludeName = {
            "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration",
            "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration",
            "org.springframework.boot.data.jpa.autoconfigure.DataJpaRepositoriesAutoConfiguration",
        })
@EnableMultiDataSourceJpa
@Configuration(proxyBeanMethods = false)
public class MultiDataSourceTestConfig {

    @Bean
    public DataSource orangeDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:orangedb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public DataSource carDataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:cardb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL")
                .username("sa")
                .password("")
                .build();
    }

    @Bean
    public JpaDataSourceDefinition orangeJpaDefinition() {
        return JpaDataSourceDefinitionBuilder.builder()
                .name("orange")
                .dataSourceBeanName("orangeDataSource")
                .entityPackage(OrangeEntity.class.getPackageName())
                .jpaProperty("hibernate.hbm2ddl.auto", "create-drop")
                .jpaProperty("hibernate.show_sql", "true")
                .build();
    }

    @Bean
    public JpaDataSourceDefinition carJpaDefinition() {
        return JpaDataSourceDefinitionBuilder.builder()
                .name("car")
                .dataSourceBeanName("carDataSource")
                .entityPackage(CarEntity.class.getPackageName())
                .jpaProperty("hibernate.hbm2ddl.auto", "create-drop")
                .jpaProperty("hibernate.show_sql", "true")
                .build();
    }
}
