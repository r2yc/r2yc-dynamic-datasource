package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.support;

import org.apache.seata.rm.datasource.DataSourceProxy;

import javax.sql.DataSource;

/**
 * seata数据源封装
 *
 * @author zhanghaoran
 */
public class SeataDataSourceFactory {

    public static DataSourceProxy createDataSourceProxy(DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }

    public static boolean isSeataDataSource(DataSource dataSource) {
        return dataSource instanceof DataSourceProxy;
    }

    public static DataSource getTargetDataSource(DataSource dataSource) {
        return ((DataSourceProxy) dataSource).getTargetDataSource();
    }
}
