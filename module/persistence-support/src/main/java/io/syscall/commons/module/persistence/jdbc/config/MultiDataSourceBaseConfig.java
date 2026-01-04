package io.syscall.commons.module.persistence.jdbc.config;

import io.syscall.commons.module.persistence.jdbc.support.InvalidDataSource;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class MultiDataSourceBaseConfig {

    @Bean(name = "NO-PRIMARY-DATASOURCE")
    DataSource dummyDataSource() {
        return new InvalidDataSource();
    }
}
