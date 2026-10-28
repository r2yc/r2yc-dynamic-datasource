package com.r2yc.dynamic.datasource.spring.boot.autoconfigure;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.context.DataSourceContextHolder;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.support.SeataDataSourceFactory;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源路由
 *
 * @author zhanghaoran
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource implements DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DynamicRoutingDataSource.class);

    /**
     * 自定义数据源集合
     * 没有选择ConcurrentHashMap，担心get时候会出现效率问题，安全问题由方法来决定
     */
    private final Map<String, DataSource> dataSourceMap = new ConcurrentHashMap<>();

    /**
     * 是否是seata代理
     */
    private final Boolean seataMode;

    /**
     * 主库
     */
    private final String primary;

    public DynamicRoutingDataSource(String primary, Boolean seataMode) {
        this.primary = primary;
        this.seataMode = seataMode;
    }

    /**
     * 新增数据源
     *
     * @param dsName     数据源名称
     * @param dataSource 数据源
     */
    public void addDataSource(String dsName, DataSource dataSource) {
        if (dataSourceMap.containsKey(dsName)) {
            logger.warn("数据源【{}】已经存在，不允许添加。 已关闭了该数据源。", dsName);
            closeDataSource(dataSource);
            return;
        }
        if (seataMode) {
            dataSourceMap.put(dsName, new ItemDataSource(dataSource, SeataDataSourceFactory.createDataSourceProxy(dataSource)));
            logger.info("动态数据源加载:Seata代理Hikari数据源【{}】", dsName);
        } else {
            dataSourceMap.put(dsName, new ItemDataSource(dataSource, dataSource));
            logger.info("动态数据源加载:Hikari数据源【{}】", dsName);
        }
    }


    /**
     * 关闭数据源
     * 正常情况下，数据源都实现了claseable接口
     *
     * @param dataSource 数据源对象
     * @return boolean
     */
    private boolean closeDataSource(DataSource dataSource) {
        if (dataSource instanceof ItemDataSource itemDataSource) {
            itemDataSource.close();
            return true;
        } else {
            try {
                Class<? extends DataSource> clazz = null;
                Method closeMethod = null;
                if (this.seataMode && SeataDataSourceFactory.isSeataDataSource(dataSource)) {
                    DataSource targetDataSource = SeataDataSourceFactory.getTargetDataSource(dataSource);
                    clazz = targetDataSource.getClass();
                    closeMethod = ReflectionUtils.findMethod(clazz, "close");
                    if (null == closeMethod) {
                        throw new NoSuchMethodException("数据源没有实现close方法,关闭失败");
                    }
                    closeMethod.invoke(targetDataSource);
                } else {
                    clazz = dataSource.getClass();
                    closeMethod = ReflectionUtils.findMethod(clazz, "close");
                    if (null == closeMethod) {
                        throw new NoSuchMethodException("数据源没有实现close方法,关闭失败");
                    }
                    closeMethod.invoke(dataSource);
                }
                return true;
            } catch (NoSuchMethodException e) {
                logger.warn(e.getMessage(), e);
            } catch (InvocationTargetException e) {
                logger.warn("数据源的close方法出现异常,关闭失败", e);
            } catch (IllegalAccessException e) {
                logger.warn("没有访问数据源close方法的权限,关闭失败", e);
            }
            return false;
        }
    }

    @Override
    protected String determineCurrentLookupKey() {
        String dataSourceKey = DataSourceContextHolder.getDataSourceKey();
        if (!StringUtils.hasText(dataSourceKey)) {
            return primary;
        }
        return dataSourceKey;
    }

    /**
     * 重写确认的目标数据源
     *
     * @return DataSource
     */
    @NullMarked
    @Override
    protected DataSource determineTargetDataSource() {
        DataSource dataSource = dataSourceMap.get(this.determineCurrentLookupKey());
        Assert.notNull(dataSource, "数据源 [" + this.determineCurrentLookupKey() + "] 不存在");
        return dataSource;
    }

    @NullMarked
    @Override
    public Connection getConnection() throws SQLException {
        return this.determineTargetDataSource().getConnection();

    }

    @NullMarked
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return this.determineTargetDataSource().getConnection(username, password);
    }

    /**
     * 销毁全部动态数据源，在应用程序正常关闭退出的时候，spring会调用该方法进行销毁，释放资源
     * 在程序关闭的时候，优雅释放数据库连接，防止动态数据源非法关闭，让正在执行的程序执行完毕。
     */
    @Override
    public synchronized void destroy() {
        this.dataSourceMap.forEach((k, v) -> {
            boolean result = closeDataSource(v);
            logger.info("程序关闭,销毁据源：[{}], {}", k, result);
        });

    }

    @NullMarked
    @Override
    public Map<Object, DataSource> getResolvedDataSources() {
        return Collections.unmodifiableMap(this.dataSourceMap);
    }


    @NullMarked
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> face) throws SQLException {
        if (face.isInstance(this)) {
            return (T) this;
        }
        return this.determineTargetDataSource().unwrap(face);
    }

    @NullMarked
    @Override
    public boolean isWrapperFor(Class<?> face) throws SQLException {
        return (this.determineTargetDataSource().isWrapperFor(face) || face.isInstance(this));
    }

    @Override
    public void afterPropertiesSet() {
        if (dataSourceMap.isEmpty()) {
            throw new IllegalArgumentException("没有配置数据源，请检查配置文件 ");
        }
    }

}
