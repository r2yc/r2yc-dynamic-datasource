package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 动态数据源配置类
 *
 * @author zhanghaoran
 */
@ConfigurationProperties(
        prefix = "spring.datasource.dynamic"
)
public class DynamicDatasourceProperties {

    /**
     * 是否启用seata
     */
    private Boolean seata = false;

    /**
     * 主库
     */
    private String primary;

    /**
     * 是否开启健康检查
     */
    private Boolean healthIndicator = true;

    /**
     * 多数据源
     */
    private Map<String, DatasourceProperties> dbs;

    public Boolean getSeata() {
        return seata;
    }

    public void setSeata(Boolean seata) {
        this.seata = seata;
    }

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public Boolean getHealthIndicator() {
        return healthIndicator;
    }

    public void setHealthIndicator(Boolean healthIndicator) {
        this.healthIndicator = healthIndicator;
    }

    public Map<String, DatasourceProperties> getDbs() {
        return dbs;
    }

    public void setDbs(Map<String, DatasourceProperties> dbs) {
        this.dbs = dbs;
    }
}
