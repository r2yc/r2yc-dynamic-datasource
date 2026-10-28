package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.context;

/**
 * 说明
 *
 * @author zhanghaoran
 */
public class DataSourceContextHolder {

    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setDataSourceKey(String dsName) {
        CONTEXT.set(dsName);
    }

    public static String getDataSourceKey() {
        return CONTEXT.get();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
