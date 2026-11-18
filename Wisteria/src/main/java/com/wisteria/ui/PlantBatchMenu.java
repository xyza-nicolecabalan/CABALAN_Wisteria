package com.wisteria.ui;

import com.wisteria.dao.PlantBatchDao;
import com.wisteria.model.PlantBatch;
import com.wisteria.model.User;

import java.util.List;
import java.util.Scanner;

public class PlantBatchMenu {

    private static final List<String> VALID_STATUSES =
            List.of("AVAILABLE", "RESERVED", "SOLD");

    private final Scanner scanner;
    private final User user;
    private final PlantBatchDao batchDao = new PlantBatchDao();

    public PlantBatchMenu(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    // ==============================
    // SHOW PLANT BATCH MENU
    // ==============================

    public void show() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("     MANAGE PLANT BATCHES");
            System.out.println("==============================");
            System.out.println("1. View All Plant Batches");
            System.out.println("2. Add New Plant Batch");
            System.out.println("3. Update Batch Status");
            System.out.println("0. Back");
            System.out.println("==============================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllBatches();
                    break;
                case "2":
                    addBatch();
                    break;
                case "3":
                    updateBatchStatus();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
            }
        }
    }

    // ==============================
    // VIEW ALL BATCHES
    // ==============================

    private void viewAllBatches() {

        System.out.println();
        System.out.println("---------- ALL PLANT BATCHES ----------");

        UiUtil.printBatchTable(batchDao.getAllBatches());
    }

    // ==============================
    // ADD NEW BATCH
    // ==============================

    private void addBatch() {

        System.out.println();
        System.out.println("---------- ADD NEW PLANT BATCH ----------");

        String species = UiUtil.readRequired(
                scanner, "Species name (0 to cancel): ");

        if (species.equals("0")) {
            System.out.println("Add batch cancelled.");
            return;
        }

        System.out.print("Mother plant source (optional, press Enter to skip): ");
        String motherSource = scanner.nextLine().trim();
        if (motherSource.isEmpty()) {
            motherSource = null;
        }

        String method = UiUtil.readRequired(
                scanner, "Propagation method (e.g., Cutting, Seed): ");

        String stage = UiUtil.readRequired(
                scanner, "Growth stage (e.g., Seedling): ");

        String zone = UiUtil.readRequired(
                scanner, "Greenhouse zone (e.g., A1): ");

        double price = UiUtil.readPositiveDouble(
                scanner, "Price (P): ");

        if (batchDao.addBatch(user.userId(), species, motherSource,
                method, stage, zone, price)) {

            System.out.println();
            System.out.println("SUCCESS: Batch added as AVAILABLE.");
        } else {
            System.out.println("Failed to add batch.");
        }
    }

    // ==============================
    // UPDATE BATCH STATUS
    // ==============================

    private void updateBatchStatus() {

        System.out.println();
        System.out.println("---------- UPDATE BATCH STATUS ----------");

        List<PlantBatch> batches = batchDao.getAllBatches();

        if (batches.isEmpty()) {
            System.out.println("No plant batches yet. Add a batch first.");
            return;
        }

        UiUtil.printBatchTable(batches);

        // ---- Step A: choose the batch ----
        PlantBatch target = null;

        while (target == null) {

            int batchId = UiUtil.readInt(
                    scanner, "Enter Batch ID to update (0 to cancel): ");

            if (batchId == 0) {
                System.out.println("Update cancelled.");
                return;
            }

            for (PlantBatch b : batches) {
                if (b.batchId() == batchId) {
                    target = b;
                    break;
                }
            }

            if (target == null) {
                System.out.println(
                        "Batch ID " + batchId + " does not exist.");
            }
        }

        // ---- Step B: choose the new status ----
        String newStatus = readValidBatchStatus();

        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (newStatus.equals(target.status())) {
            System.out.println("Batch #" + target.batchId()
                    + " is already " + newStatus + ". No change made.");
            return;
        }

        // ---- Step C: save (also writes the BATCH_UPDATED log) ----
        if (batchDao.updateBatchStatus(
                user.userId(), target.batchId(), newStatus)) {

            System.out.println();
            System.out.println("SUCCESS: Batch #" + target.batchId()
                    + " status changed from " + target.status()
                    + " to " + newStatus + ".");
        } else {
            System.out.println("Update failed.");
        }
    }

    // Returns a valid batch status, or null if the user typed 0 to cancel.
    private String readValidBatchStatus() {

        while (true) {

            System.out.print(
                    "Enter new status (AVAILABLE / RESERVED / SOLD, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                return null;
            }

            if (VALID_STATUSES.contains(input)) {
                return input;
            }

            System.out.println(
                    "Invalid status. Choose AVAILABLE, RESERVED, or SOLD.");
        }
    }
}