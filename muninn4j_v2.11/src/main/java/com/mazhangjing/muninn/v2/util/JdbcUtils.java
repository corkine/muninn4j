package com.mazhangjing.muninn.v2.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JdbcUtils {

    public static Connection getConnection() {
        Connection connection = null;

        InputStream in = JdbcUtils.class.getClassLoader().getResourceAsStream("database.properties");
        Properties properties = new Properties();
        try {
            properties.load(in);

            String url = null;
            String user = null;
            String password = null;
            url = properties.getProperty("jdbc.jdbcUrl");
            user = properties.getProperty("jdbc.user");
            password = properties.getProperty("jdbc.password");

            Class.forName(properties.getProperty("jdbc.driverClass"));

            in.close();
            connection =  DriverManager.getConnection(url,user,password);
        } catch (IOException | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void release(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
