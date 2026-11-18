package com.wisteria.dao;

import com.wisteria.model.PlantBatch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlantBatchDao {

    // Used to write audit entries for batch events
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    // ==============================
    // ADD PLANT BATCH
    // ==============================

    public boolean addBatch(
            int staffId,
            String speciesName,
            String motherPlantSource,
            String propagationMethod,
            String growthStage,
            String greenhouseZone,
            double price) {

        String sql = """
                INSERT INTO plant_batches
                (species_name, mother_plant_source, propagation_method,
                 growth_stage, greenhouse_zone, price, status, created_at)
                VALUES (?, ?, ?, ?, ?, ?, 'AVAILABLE', ?)
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, speciesName);
            statement.setString(2, motherPlantSource); // may be null
            statement.setString(3, propagationMethod);
            statement.setString(4, growthStage);
            statement.setString(5, greenhouseZone);
            statement.setDouble(6, price);
            statement.setString(7, LocalDateTime.now().toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding plant batch.");
            e.printStackTrace();
            return false;
        }

        // Audit log (connection is already closed here)
        activityLogDao.logAction(
                staffId,
                "BATCH_ADDED",
                "User " + staffId + " added batch: " + speciesName);

        return true;
    }

    // ==============================
    // GET ALL BATCHES
    // ==============================

    public List<PlantBatch> getAllBatches() {

        String sql = """
                SELECT batch_id, species_name, mother_plant_source,
                       propagation_method, growth_stage, greenhouse_zone,
                       price, status, created_at
                FROM plant_batches
                ORDER BY batch_id
                """;

        return fetchBatches(sql);
    }

    // ==============================
    // GET AVAILABLE BATCHES
    // ==============================

    public List<PlantBatch> getAvailableBatches() {

        String sql = """
                SELECT batch_id, species_name, mother_plant_source,
                       propagation_method, growth_stage, greenhouse_zone,
                       price, status, created_at
                FROM plant_batches
                WHERE status = 'AVAILABLE'
                ORDER BY batch_id
                """;

        return fetchBatches(sql);
    }

    // ==============================
    // FIND BATCH BY ID
    // ==============================

    public PlantBatch findById(int batchId) {

        String sql = """
                SELECT batch_id, species_name, mother_plant_source,
                       propagation_method, growth_stage, greenhouse_zone,
                       price, status, created_at
                FROM plant_batches
                WHERE batch_id = ?
                """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, batchId);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return mapBatch(result);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding plant batch.");
            e.printStackTrace();
        }

        return null;
    }

    // ==============================
    // UPDATE BATCH STATUS (LIFECYCLE / STATE CHANGE)
    // ==============================

    public boolean updateBatchStatus(
            int userId,
            int batchId,
            String newStatus) {

        PlantBatch batch = findById(batchId);

        if (batch == null) {
            return false;
        }

        String oldStatus = batch.status();
        String statusToSave = newStatus.toUpperCase();

        String sql = """
                UPDATE plant_batches
                SET status = ?
                WHERE batch_id = ?
                """;

        boolean success;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, statusToSave);
            statement.setInt(2, batchId);

            success = statement.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating batch status.");
            e.printStackTrace();
            return false;
        }

        if (success) {
            activityLogDao.logAction(
                    userId,
                    "BATCH_UPDATED",
                    "Updated Batch #" + batchId + " status from "
                            + oldStatus + " to " + statusToSave);
        }

        return success;
    }

    // ==============================
    // HELPER: run a no-parameter batch query
    // ==============================

    private List<PlantBatch> fetchBatches(String sql) {

        List<PlantBatch> batches = new ArrayList<>();

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet result = statement.executeQuery()
        ) {

            while (result.next()) {
                batches.add(mapBatch(result));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving plant batches.");
            e.printStackTrace();
        }

        return batches;
    }

    // ==============================
    // HELPER: ResultSet -> PlantBatch
    // ==============================

    private PlantBatch mapBatch(ResultSet result) throws SQLException {
        return new PlantBatch(
                result.getInt("batch_id"),
                result.getString("species_name"),
                result.getString("mother_plant_source"),
                result.getString("propagation_method"),
                result.getString("growth_stage"),
                result.getString("greenhouse_zone"),
                result.getDouble("price"),
                result.getString("status"),
                result.getString("created_at")
        );
    }
}