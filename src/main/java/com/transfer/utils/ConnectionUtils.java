package com.transfer.utils;

import com.annotation.Service;

import java.sql.Connection;
import java.sql.SQLException;


@Service
public class ConnectionUtils {

    private ThreadLocal<Connection> threadLocal = new ThreadLocal<Connection>();

    private static ConnectionUtils connectionUtils = new ConnectionUtils();

    public ConnectionUtils() {
    }

    public static ConnectionUtils getInstance() {
        return connectionUtils;
    }

    public Connection getCurrentConnection() throws SQLException {
        Connection connection = threadLocal.get();
        if (connection == null) {
            connection = DruidUtils.getInstance().getConnection();
            threadLocal.set(connection);
        }
        return connection;
    }
}
