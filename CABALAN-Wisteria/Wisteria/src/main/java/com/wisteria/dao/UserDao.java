package com.wisteria.dao;

import com.wisteria.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    private static final String USER_COLUMNS = """
            user_id, full_name, email, password_hash,
            role, status, created_at
            """;

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
                (full_name, email, password_hash, role, status, created_at)
                VALUES (?, ?, ?, ?, 'PENDING', ?)
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

        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE email = ? AND password_hash = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapUser(result);
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

        String sql = "SELECT " + USER_COLUMNS + " FROM users WHERE email = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapUser(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding user.");
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // GET ALL USERS
    // ==============================

    public List<User> getAllUsers() {

        String sql = "SELECT " + USER_COLUMNS + " FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                users.add(mapUser(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users.");
            e.printStackTrace();
        }

        return users;
    }

    // ==============================
    // GET PENDING USERS
    // ==============================

    public List<User> getPendingUsers() {

        String sql = "SELECT " + USER_COLUMNS
                + " FROM users WHERE status = 'PENDING' ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                users.add(mapUser(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving pending users.");
            e.printStackTrace();
        }

        return users;
    }

    // ==============================
    // APPROVE USER
    // ==============================

    public boolean approveUser(int userId) {

        String sql = "UPDATE users SET status = 'APPROVED' WHERE user_id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error approving user.");
            e.printStackTrace();
            return false;
        }
    }

    // ==============================
    // HELPER: ResultSet -> User
    // ==============================

    private User mapUser(ResultSet result) throws SQLException {
        return new User(
                result.getInt("user_id"),
                result.getString("full_name"),
                result.getString("email"),
                result.getString("password_hash"),
                result.getString("role"),
                result.getString("status"),
                result.getString("created_at")
        );
    }
}