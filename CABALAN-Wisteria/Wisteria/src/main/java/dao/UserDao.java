```java
        package com.wisteria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserDao {

    // Create the users table
    public void createTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    roles TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'PENDING'
                )
                """;

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Users table is ready.");

        } catch (Exception e) {
            System.out.println("Error creating users table: "
                    + e.getMessage());
        }
    }

    // Register a new user
    public boolean register(String username, String password,
                            String role) {

        String sql = """
                INSERT INTO users (username, password, roles, status)
                VALUES (?, ?, ?, 'PENDING')
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            pstmt.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Registration failed: "
                    + e.getMessage());
            return false;
        }
    }

    // Check login credentials
    public boolean login(String username, String password) {

        String sql = """
                SELECT status FROM users
                WHERE username = ? AND password = ?
                """;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");

                    if ("APPROVED".equals(status)) {
                        return true;
                    } else {
                        System.out.println(
                                "Your account is pending approval.");
                        return false;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
        }

        return false;
    }
}
```