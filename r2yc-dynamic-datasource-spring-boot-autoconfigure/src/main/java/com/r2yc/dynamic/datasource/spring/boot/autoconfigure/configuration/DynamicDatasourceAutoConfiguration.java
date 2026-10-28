package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.configuration;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.DynamicRoutingDataSource;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.aspect.LocalTransactionalAspect;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.aspect.MapperDatasourceAspect;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.creator.HikariDataSourceCreator;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.health.DynamicDataSourceHealthIndicator;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.health.HealthCheckAdapter;
import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.properties.DynamicDatasourceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.health.autoconfigure.contributor.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 多数据源装配类
 *
 * @author zhanghaoran
 */
@Configuration(proxyBeanMethods = false)
@AutoConfiguration(before = {DataSourceAutoConfiguration.class})
@EnableConfigurationProperties({DynamicDatasourceProperties.class})
@Import({DynamicDatasourceAutoConfiguration.DynamicDataSourceHealthConfiguration.class})
public class DynamicDatasourceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDatasourceAutoConfiguration.class);

    /**
     * 覆盖系统的数据库，初始化动态数据源
     *
     * @param dynamicDatasourceProperties 数据源配置{@link DynamicDatasourceProperties}
     * @return DynamicRoutingDataSource 动态数据源
     */
    @Bean
    DynamicRoutingDataSource dataSource(DynamicDatasourceProperties dynamicDatasourceProperties) {
        DynamicRoutingDataSource dynamicRoutingDataSource = new DynamicRoutingDataSource(
                dynamicDatasourceProperties.getPrimary(), dynamicDatasourceProperties.getSeata());
        dynamicDatasourceProperties.getDbs().forEach((k, v) -> {
            dynamicRoutingDataSource.addDataSource(k, HikariDataSourceCreator.createDataSource(k, v));
        });
        return dynamicRoutingDataSource;
    }


    @ConditionalOnClass({AbstractHealthIndicator.class})
    @ConditionalOnProperty(prefix = "spring.datasource.dynamic", name = "health-indicator", havingValue = "true", matchIfMissing = true)
    @Configuration(proxyBeanMethods = false)
    static class DynamicDataSourceHealthConfiguration {


        /**
         * 健康检查
         *
         * @param dataSource 数据源
         * @return DynamicDataSourceHealthIndicator
         */
        @Bean("dynamicDataSourceHealthCheck")
        @ConditionalOnEnabledHealthIndicator("dynamicDS")
        public DynamicDataSourceHealthIndicator healthIndicator(DynamicRoutingDataSource dataSource) {
            DynamicDataSourceHealthIndicator dynamicDataSourceHealthIndicator =
                    new DynamicDataSourceHealthIndicator(dataSource, new HealthCheckAdapter());
            logger.info("Bean of DynamicDataSourceHealthIndicator has already created");
            return dynamicDataSourceHealthIndicator;
        }
    }

    @Bean
    LocalTransactionalAspect localTransactionalAspect() {
        return new LocalTransactionalAspect();
    }

    @Bean
    MapperDatasourceAspect mapperDatasourceAspect() {
        return new MapperDatasourceAspect();
    }
}
