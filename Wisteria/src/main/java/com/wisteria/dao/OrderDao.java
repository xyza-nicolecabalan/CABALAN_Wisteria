package com.wisteria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Locale;

public class OrderDao {

    // Used to write the audit entry for each purchase
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    // ==============================
    // PROCESS PURCHASE (CORE BUSINESS PROCESS)
    // ==============================
    // Runs as ONE transaction: check availability, mark the batch SOLD,
    // and record the order. If any step fails, nothing is saved.

    public boolean processPurchase(int userId, int batchId) {

        String selectSql = """
                SELECT price, status
                FROM plant_batches
                WHERE batch_id = ?
                """;

        String updateSql = """
                UPDATE plant_batches
                SET status = 'SOLD'
                WHERE batch_id = ? AND status = 'AVAILABLE'
                """;

        String insertSql = """
                INSERT INTO orders
                (user_id, batch_id, transaction_type,
                 total_amount, order_status, created_at)
                VALUES (?, ?, 'PURCHASE', ?, 'COMPLETED', ?)
                """;

        double total = 0;

        try (Connection connection = DatabaseConnection.getConnection()) {

            connection.setAutoCommit(false);

            try {

                // Step 1: read the price and confirm the batch is available
                try (PreparedStatement select =
                             connection.prepareStatement(selectSql)) {

                    select.setInt(1, batchId);

                    try (ResultSet result = select.executeQuery()) {

                        if (!result.next()
                                || !"AVAILABLE".equals(result.getString("status"))) {
                            connection.rollback();
                            return false;
                        }

                        total = result.getDouble("price");
                    }
                }

                // Step 2: mark the batch as SOLD
                try (PreparedStatement update =
                             connection.prepareStatement(updateSql)) {

                    update.setInt(1, batchId);

                    if (update.executeUpdate() == 0) {
                        connection.rollback();
                        return false;
                    }
                }

                // Step 3: record the order
                try (PreparedStatement insert =
                             connection.prepareStatement(insertSql)) {

                    insert.setInt(1, userId);
                    insert.setInt(2, batchId);
                    insert.setDouble(3, total);
                    insert.setString(4, LocalDateTime.now().toString());
                    insert.executeUpdate();
                }

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            System.out.println("Purchase failed.");
            e.printStackTrace();
            return false;
        }

        // Audit log (connection is already closed here)
        activityLogDao.logAction(
                userId,
                "PLANT_PURCHASED",
                String.format(Locale.US,
                        "Processed purchase of Batch #%d for Customer %d - Total: P%.2f",
                        batchId, userId, total));

        return true;
    }
}