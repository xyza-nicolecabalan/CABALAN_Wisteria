package com.wisteria.dao;

import com.wisteria.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class UserDao {

    // ==============================
    // REGISTER USER
    // ==============================

    public boolean registerUser(
            String fullName,
            String email,
            String password,
            String role) {

        String sql = """
                INSERT INTO users
                (full_name, email, password_hash, role, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, password);
            statement.setString(4, role);
            statement.setString(5, LocalDateTime.now().toString());

            statement.executeUpdate();

            return true;

        } catch (SQLException e) {

            if (e.getMessage() != null &&
                    e.getMessage().toLowerCase().contains("unique")) {

                System.out.println("Email address is already registered.");

            } else {

                System.out.println("Registration failed.");
                e.printStackTrace();
            }

            return false;
        }
    }


    // ==============================
    // LOGIN USER
    // ==============================

    public User login(String email, String password) {

        String sql = """
                SELECT
                    user_id,
                    full_name,
                    email,
                    password_hash,
                    role,
                    created_at
                FROM users
                WHERE email = ?
                AND password_hash = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new User(
                            result.getInt("user_id"),
                            result.getString("full_name"),
                            result.getString("email"),
                            result.getString("password_hash"),
                            result.getString("role"),
                            result.getString("created_at")
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println("Login error.");
            e.printStackTrace();
        }

        return null;
    }


    // ==============================
    // FIND USER BY EMAIL
    // ==============================

    public User findByEmail(String email) {

        String sql = """
                SELECT
                    user_id,
                    full_name,
                    email,
                    password_hash,
                    role,
                    created_at
                FROM users
                WHERE email = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {

                if (result.next()) {

                    return new User(
                            result.getInt("user_id"),
                            result.getString("full_name"),
                            result.getString("email"),
                            result.getString("password_hash"),
                            result.getString("role"),
                            result.getString("created_at")
                    );
                }
            }

        } catch (SQLException e) {

            System.out.println("Error finding user.");
            e.printStackTrace();
        }

        return null;
    }
}