package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.properties;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 数据源配置
 *
 * @author zhanghaoran
 */
public class DatasourceProperties {

    /**
     * 数据库名称, 创建时指定数据源名称不能重复, 对应系统中每个租户的key。
     * default;
     */
    private String dsName;
    /**
     * 数据源类型 druid, hikari等
     * com.zaxxer.hikari.HikariDataSource
     */
    private String type;
    /**
     * 驱动名称
     * com.mysql.cj.jdbc.Driver
     */
    private String driverClassName;
    /**
     * 数据库地址
     */
    private String url;
    /**
     * 数据库用户名
     * user-test
     */
    private String username;
    /**
     * 数据库密码
     * user-test-pwd
     */
    private String password;

    @NestedConfigurationProperty
    private HikariConfig hikari = new HikariConfig();

    public String getDsName() {
        return dsName;
    }

    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public HikariConfig getHikari() {
        return hikari;
    }

    public void setHikari(HikariConfig hikari) {
        this.hikari = hikari;
    }
}
