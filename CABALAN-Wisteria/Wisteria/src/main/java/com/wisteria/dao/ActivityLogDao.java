package com.wisteria.dao;

import com.wisteria.model.ActivityLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDao {

    // Clean, fixed-width timestamp: 2026-11-03 14:25:09
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // LOG ACTION

    public boolean logAction(
            int userId,
            String actionType,
            String description) {

        String sql = """
                INSERT INTO activity_logs
                (user_id, action_type, description, timestamp)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);
            statement.setString(2, actionType);
            statement.setString(3, description);
            statement.setString(4, LocalDateTime.now().format(TIME_FORMAT));

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Warning: could not write to the activity log.");
            e.printStackTrace();
            return false;
        }
    }

    // GET ALL LOGS

    public List<ActivityLog> getAllLogs() {

        String sql = """
                SELECT log_id, user_id, action_type, description, timestamp
                FROM activity_logs
                ORDER BY timestamp DESC, log_id DESC
                """;

        List<ActivityLog> logs = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                logs.add(mapLog(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving activity logs.");
            e.printStackTrace();
        }

        return logs;
    }

    // GET LOGS BY USER ID

    public List<ActivityLog> getLogsByUserId(int userId) {

        String sql = """
                SELECT log_id, user_id, action_type, description, timestamp
                FROM activity_logs
                WHERE user_id = ?
                ORDER BY timestamp DESC, log_id DESC
                """;

        List<ActivityLog> logs = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    logs.add(mapLog(result));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving logs for user.");
            e.printStackTrace();
        }

        return logs;
    }

    // CLEAR ALL LOGS

    public boolean clearAllLogs() {

        String sql = "DELETE FROM activity_logs";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error clearing activity logs.");
            e.printStackTrace();
            return false;
        }
    }

    // HELPER: ResultSet -> ActivityLog

    private ActivityLog mapLog(ResultSet result) throws SQLException {
        return new ActivityLog(
                result.getInt("log_id"),
                result.getInt("user_id"),
                result.getString("action_type"),
                result.getString("description"),
                result.getString("timestamp")
        );
    }
}
