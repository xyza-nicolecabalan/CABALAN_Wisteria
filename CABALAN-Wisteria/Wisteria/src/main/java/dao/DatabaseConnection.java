package com.wisteria.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:wisteria.db";

    public static Connection connect() {

        try {
            Connection connection = DriverManager.getConnection(URL);
            System.out.println("Connected to SQLite successfully!");
            return connection;

        } catch (Exception e) {
            System.out.println("Failed to connect to SQLite.");
            System.out.println(e.getMessage());
            return null;
        }
    }
}