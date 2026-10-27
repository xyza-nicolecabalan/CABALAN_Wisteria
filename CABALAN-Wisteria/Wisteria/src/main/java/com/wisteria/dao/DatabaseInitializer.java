package com.wisteria.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {

        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    full_name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """;

        String createPlantBatchesTable = """
                CREATE TABLE IF NOT EXISTS plant_batches (
                    batch_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    species_name TEXT NOT NULL,
                    mother_plant_source TEXT,
                    propagation_method TEXT NOT NULL,
                    growth_stage TEXT NOT NULL,
                    greenhouse_zone TEXT NOT NULL,
                    price REAL NOT NULL,
                    status TEXT NOT NULL,
                    created_at TEXT NOT NULL
                )
                """;

        String createOrdersTable = """
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    batch_id INTEGER NOT NULL,
                    transaction_type TEXT NOT NULL,
                    total_amount REAL NOT NULL,
                    order_status TEXT NOT NULL,
                    created_at TEXT NOT NULL,

                    FOREIGN KEY (user_id)
                        REFERENCES users(user_id),

                    FOREIGN KEY (batch_id)
                        REFERENCES plant_batches(batch_id)
                )
                """;

        String createCareLogsTable = """
                CREATE TABLE IF NOT EXISTS care_logs (
                    care_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    batch_id INTEGER NOT NULL,
                    staff_id INTEGER NOT NULL,
                    activity_type TEXT NOT NULL,
                    temperature_celsius REAL,
                    humidity_percent REAL,
                    notes TEXT,
                    performed_at TEXT NOT NULL,

                    FOREIGN KEY (batch_id)
                        REFERENCES plant_batches(batch_id),

                    FOREIGN KEY (staff_id)
                        REFERENCES users(user_id)
                )
                """;

        String createHealthIncidentsTable = """
                CREATE TABLE IF NOT EXISTS health_incidents (
                    incident_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    batch_id INTEGER NOT NULL,
                    reported_by INTEGER NOT NULL,
                    issue_type TEXT NOT NULL,
                    severity TEXT NOT NULL,
                    treatment_plan TEXT,
                    status TEXT NOT NULL,
                    reported_at TEXT NOT NULL,

                    FOREIGN KEY (batch_id)
                        REFERENCES plant_batches(batch_id),

                    FOREIGN KEY (reported_by)
                        REFERENCES users(user_id)
                )
                """;

        String createActivityLogsTable = """
                CREATE TABLE IF NOT EXISTS activity_logs (
                    log_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    action_description TEXT NOT NULL,
                    timestamp TEXT NOT NULL,

                    FOREIGN KEY (user_id)
                        REFERENCES users(user_id)
                )
                """;

        try (
                Connection connection = com.wisteria.dao.DatabaseConnection.getConnection();
                Statement statement = connection.createStatement()
        ) {

            statement.execute(createUsersTable);
            statement.execute(createPlantBatchesTable);
            statement.execute(createOrdersTable);
            statement.execute(createCareLogsTable);
            statement.execute(createHealthIncidentsTable);
            statement.execute(createActivityLogsTable);

            System.out.println();
            System.out.println("=================================");
            System.out.println(" WISTERIA DATABASE INITIALIZED");
            System.out.println("=================================");

        } catch (SQLException e) {

            System.out.println();
            System.out.println("Database initialization failed.");
            e.printStackTrace();
        }
    }
}