package com.example.demo.config.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReadWriteDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.write")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.read")
    public DataSourceProperties readDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("writeDataSource")
    @ConfigurationProperties("spring.datasource.write.hikari")
    public HikariDataSource writeDataSource(
            @Qualifier("writeDataSourceProperties") DataSourceProperties writeDataSourceProperties) {
        return writeDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean("readDataSource")
    @ConfigurationProperties("spring.datasource.read.hikari")
    public HikariDataSource readDataSource(
            @Qualifier("readDataSourceProperties") DataSourceProperties readDataSourceProperties,
            @Qualifier("writeDataSourceProperties") DataSourceProperties writeDataSourceProperties) {

        // If read replica is not configured, safely fall back to primary.
        if (!StringUtils.hasText(readDataSourceProperties.getUrl())) {
            readDataSourceProperties.setUrl(writeDataSourceProperties.getUrl());
        }
        if (!StringUtils.hasText(readDataSourceProperties.getUsername())) {
            readDataSourceProperties.setUsername(writeDataSourceProperties.getUsername());
        }
        if (!StringUtils.hasText(readDataSourceProperties.getPassword())) {
            readDataSourceProperties.setPassword(writeDataSourceProperties.getPassword());
        }
        if (!StringUtils.hasText(readDataSourceProperties.getDriverClassName())) {
            readDataSourceProperties.setDriverClassName(writeDataSourceProperties.getDriverClassName());
        }

        return readDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public DataSource dataSource(
            @Qualifier("writeDataSource") DataSource writeDataSource,
            @Qualifier("readDataSource") DataSource readDataSource) {

        ReadWriteRoutingDataSource routingDataSource = new ReadWriteRoutingDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceRole.WRITE, writeDataSource);
        targetDataSources.put(DataSourceRole.READ, readDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(writeDataSource);
        routingDataSource.afterPropertiesSet();
        return routingDataSource;
    }
}