/*
 * Copyright © 2018 organization baomidou
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.r2yc.dynamic.datasource.spring.boot.autoconfigure;

import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.AbstractDataSource;
import org.springframework.util.ReflectionUtils;

import javax.sql.DataSource;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zhanghaoran
 */
@NullMarked
public class ItemDataSource extends AbstractDataSource implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(ItemDataSource.class);
    private final DataSource realDataSource;
    private final DataSource targetDataSource;

    public ItemDataSource(DataSource realDataSource, DataSource targetDataSource) {
        this.realDataSource = realDataSource;
        this.targetDataSource = targetDataSource;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return targetDataSource.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return targetDataSource.getConnection(username, password);
    }

    @Override
    public boolean isWrapperFor(Class<?> face) throws SQLException {
        return super.isWrapperFor(face) || face.isInstance(realDataSource) || face.isInstance(targetDataSource);
    }

    @Override
    public <T> T unwrap(Class<T> face) throws SQLException {
        Object candidate;
        if (face.isInstance(this)) {
            candidate = this;
        } else if (face.isInstance(realDataSource)) {
            candidate = realDataSource;
        } else if (face.isInstance(targetDataSource)) {
            candidate = targetDataSource;
        } else {
            throw new SQLException("Cannot unwrap to " + face.getName());
        }
        return face.cast(candidate);
    }

    @Override
    public void close() {
        Class<? extends DataSource> clazz = realDataSource.getClass();
        try {
            Method closeMethod = ReflectionUtils.findMethod(clazz, "close");
            if (closeMethod != null) {
                closeMethod.invoke(realDataSource);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("数据源关闭异常", e);
        }
    }
}
