package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.health;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 给spring得actuator增加健康检查得钩子
 *
 * @author zhanghaoran
 */
public class HealthCheckAdapter {
    /**
     * 维护数据源健康状况
     */
    private static final Map<String, Boolean> DB_HEALTH = new ConcurrentHashMap<>();

    public void putHealth(String key, Boolean healthState) {
        DB_HEALTH.put(key, healthState);
    }

    /**
     * 获取数据源连接健康状况
     *
     * @param dataSource 数据源名称
     * @return 健康状况
     */
    public boolean getHealth(String dataSource) {
        Boolean isHealth = DB_HEALTH.get(dataSource);
        return isHealth != null && isHealth;
    }
}
