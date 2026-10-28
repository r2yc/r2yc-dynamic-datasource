package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.health;

import com.r2yc.dynamic.datasource.spring.boot.autoconfigure.DynamicRoutingDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.health.contributor.AbstractHealthIndicator;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * actuator健康检查实现类
 *
 * @author zhanghaoran
 */
public class DynamicDataSourceHealthIndicator extends AbstractHealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DynamicDataSourceHealthIndicator.class);
    private final HealthCheckAdapter healthCheckAdapter;
    private final DynamicRoutingDataSource dynamicRoutingDataSource;

    public static String TEST_QUERY_SQL = "SELECT 1";

    public DynamicDataSourceHealthIndicator(DynamicRoutingDataSource dynamicRoutingDataSource, HealthCheckAdapter healthCheckAdapter) {
        this.dynamicRoutingDataSource = dynamicRoutingDataSource;
        this.healthCheckAdapter = healthCheckAdapter;
    }

    @NullMarked
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        Iterator<Entry<Object, DataSource>> iterator = this.dynamicRoutingDataSource.getResolvedDataSources().entrySet().iterator();
        String key = null;
        Entry<Object, DataSource> next = null;
        boolean resultAvailable = false;
        while (iterator.hasNext()) {
            next = iterator.next();
            key = next.getKey().toString();
            try {
                resultAvailable = this.queryAvailable(next.getValue());
            } catch (Throwable var12) {
                resultAvailable = false;
            } finally {
                this.healthCheckAdapter.putHealth(key, resultAvailable);
                builder.withDetail(key, resultAvailable);
                if (!resultAvailable) {
                    logger.error("租户数据源异常，key：{}", key);
                    builder.status(Status.DOWN);
                } else {
                    builder.status(Status.UP);
                }
            }
        }
    }

    private Boolean queryAvailable(DataSource dataSource) throws SQLException {
        ResultSet resultSet = null;
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            resultSet = statement.executeQuery(hikariDataSource.getConnectionTestQuery());
        } else {
            resultSet = statement.executeQuery(TEST_QUERY_SQL);
        }
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columns = metaData.getColumnCount();
        if (columns != 1) {
            throw new IncorrectResultSetColumnCountException(1, columns);
        }
        connection.close();
        return true;
    }
}