package com.wisteria.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:wisteria.db";

    public static Connection getConnection() throws SQLException {

        Connection connection = DriverManager.getConnection(URL);

        // Enable SQLite foreign key constraints
        try (var statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}