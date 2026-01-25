package io.syscall.hsw.study.sampledomain.datasource.config;

import com.zaxxer.hikari.HikariDataSource;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

@AutoConfiguration
public class SampleDomainDataSourceConfiguration {

    @Qualifier("sample")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("project.datasource.sample")
    public DataSourceProperties sampleDataSourceProperties() {
        var bean = new DataSourceProperties();
        bean.setName("sample");
        bean.setType(HikariDataSource.class);
        return bean;
    }

    @Qualifier("sample")
    @Bean(defaultCandidate = false)
    @ConfigurationProperties("project.datasource.sample.hikaricp")
    public HikariDataSource sampleDataSource(@Qualifier("sample") DataSourceProperties props) {
        Assert.isAssignable(HikariDataSource.class, props.getType(), "DataSource type must be HikariDataSource");

        var hikariThreadFactory =
                Thread.ofVirtual().name(props.getName() + ":hikaricp-", 0).factory();

        var bean =
                props.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        bean.setPoolName(props.getName());
        bean.setThreadFactory(hikariThreadFactory);
        bean.setConnectionTimeout(Duration.ofSeconds(2).toMillis());
        bean.setAutoCommit(false);

        return bean;
    }
}
