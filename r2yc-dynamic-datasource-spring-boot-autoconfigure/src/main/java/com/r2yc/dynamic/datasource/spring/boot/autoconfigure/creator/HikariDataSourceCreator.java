package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.creator;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.properties.DatasourceProperties;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

/**
 * Hikari 数据源生成器
 *
 * @author zhanghaoran
 */
public class HikariDataSourceCreator {

    public static final String POOL_NAME_PREFIX = "HikariPool-";

    /**
     * 创建数据源
     *
     * @param datasourceProperties 数据源配置{@link DatasourceProperties}
     * @return HikariDataSource
     */
    public static DataSource createDataSource(String dsName, DatasourceProperties datasourceProperties) {
        HikariConfig hikariConfig = new HikariConfig();
        datasourceProperties.getHikari().copyStateTo(hikariConfig);
        hikariConfig.setPoolName(POOL_NAME_PREFIX + dsName);
        hikariConfig.setJdbcUrl(datasourceProperties.getUrl());
        hikariConfig.setUsername(datasourceProperties.getUsername());
        hikariConfig.setPassword(datasourceProperties.getPassword());
        hikariConfig.setDriverClassName(datasourceProperties.getDriverClassName());
        return new HikariDataSource(hikariConfig);
    }
}
