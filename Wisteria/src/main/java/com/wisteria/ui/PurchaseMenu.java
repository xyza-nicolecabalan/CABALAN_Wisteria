package com.wisteria.ui;

import com.wisteria.dao.OrderDao;
import com.wisteria.dao.PlantBatchDao;
import com.wisteria.model.PlantBatch;
import com.wisteria.model.User;

import java.util.List;
import java.util.Scanner;

public class PurchaseMenu {

    private final Scanner scanner;
    private final User user;
    private final PlantBatchDao batchDao = new PlantBatchDao();
    private final OrderDao orderDao = new OrderDao();

    public PurchaseMenu(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    // ==============================
    // BROWSE PLANT CATALOG
    // ==============================

    public void browseCatalog() {

        System.out.println();
        System.out.println("------- AVAILABLE PLANT BATCHES -------");

        UiUtil.printBatchTable(batchDao.getAvailableBatches());
    }

    // ==============================
    // PURCHASE A PLANT BATCH
    // ==============================

    public void purchasePlant() {

        System.out.println();
        System.out.println("---------- PURCHASE PLANT ----------");

        List<PlantBatch> available = batchDao.getAvailableBatches();

        if (available.isEmpty()) {
            System.out.println("No plant batches are available right now.");
            return;
        }

        UiUtil.printBatchTable(available);

        // ---- Step A: choose the batch ----
        PlantBatch target = null;

        while (target == null) {

            int batchId = UiUtil.readInt(
                    scanner, "Enter Batch ID to purchase (0 to cancel): ");

            if (batchId == 0) {
                System.out.println("Purchase cancelled.");
                return;
            }

            for (PlantBatch b : available) {
                if (b.batchId() == batchId) {
                    target = b;
                    break;
                }
            }

            if (target == null) {
                System.out.println("Batch ID " + batchId
                        + " is not in the available list.");
            }
        }

        // ---- Step B: confirm ----
        System.out.println();
        System.out.println("Batch #" + target.batchId() + " - "
                + target.speciesName());
        System.out.println("Total: " + UiUtil.money(target.price()));
        System.out.print("Confirm purchase? (Y to confirm, anything else cancels): ");

        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Purchase cancelled.");
            return;
        }

        // ---- Step C: process (also writes the PLANT_PURCHASED log) ----
        if (orderDao.processPurchase(user.userId(), target.batchId())) {

            System.out.println();
            System.out.println("SUCCESS: Purchase complete!");
            System.out.println("Batch #" + target.batchId() + " - "
                    + target.speciesName());
            System.out.println("Total paid: " + UiUtil.money(target.price()));
        } else {
            System.out.println();
            System.out.println(
                    "Purchase failed. The batch may no longer be available.");
        }
    }
}