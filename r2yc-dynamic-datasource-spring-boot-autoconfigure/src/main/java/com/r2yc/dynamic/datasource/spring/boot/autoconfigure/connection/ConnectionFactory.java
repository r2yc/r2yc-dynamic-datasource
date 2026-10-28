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
package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据库连接工厂
 *
 * @author zhanghaoran
 */
public class ConnectionFactory {

    private static final ThreadLocal<Map<String, Connection>> CONNECTION_HOLDER =
            new InheritableThreadLocal<>() {
                @Override
                protected Map<String, Connection> initialValue() {
                    return new ConcurrentHashMap<>(8);
                }
            };

    public static void putConnection(String ds, Connection connection) throws SQLException {
        Map<String, Connection> concurrentHashMap = CONNECTION_HOLDER.get();
        if (!concurrentHashMap.containsKey(ds)) {
            connection.setAutoCommit(true);
            concurrentHashMap.put(ds, connection);
        }
    }

    public static Connection getConnection(String ds) {
        return CONNECTION_HOLDER.get().get(ds);
    }

    public static void clean() {
        CONNECTION_HOLDER.remove();
    }
}
