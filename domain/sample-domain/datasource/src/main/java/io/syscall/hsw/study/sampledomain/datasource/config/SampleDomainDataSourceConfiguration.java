package io.syscall.hsw.study.sampledomain.datasource.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class SampleDomainDataSourceConfiguration {

    @Qualifier("sample")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("project.datasource.sample")
    public DataSourceProperties sampleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Qualifier("sample")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("project.datasource.sample.hikaricp")
    public HikariDataSource sampleDataSource(@Qualifier("sample") DataSourceProperties dataSourceProperties) {
        var bean = dataSourceProperties
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        bean.setAutoCommit(false);
        return bean;
    }
}
