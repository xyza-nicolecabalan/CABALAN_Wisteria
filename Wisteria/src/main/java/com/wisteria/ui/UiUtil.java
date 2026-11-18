package com.wisteria.ui;

import com.wisteria.model.PlantBatch;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// Small shared helpers for the console menus
public final class UiUtil {

    private UiUtil() {
    }

    // Keeps asking until the user types a whole number. Never crashes.
    public static int readInt(Scanner scanner, String prompt) {

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter a number (digits only).");
            }
        }
    }

    // Keeps asking until the user types an amount greater than 0.
    public static double readPositiveDouble(Scanner scanner, String prompt) {

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(input);

                if (Double.isFinite(value) && value > 0) {
                    return value;
                }

                System.out.println("Amount must be greater than 0.");

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid input. Please enter an amount (e.g., 850 or 850.50).");
            }
        }
    }

    // Keeps asking until the user types something (not blank).
    public static String readRequired(Scanner scanner, String prompt) {

        while (true) {

            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            if (!input.isEmpty()) {
                return input;
            }

            System.out.println("This field is required.");
        }
    }

    public static String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }

    public static String money(double amount) {
        return String.format(Locale.US, "P%.2f", amount);
    }

    // Aligned table of plant batches
    public static void printBatchTable(List<PlantBatch> batches) {

        if (batches.isEmpty()) {
            System.out.println("No plant batches found.");
            return;
        }

        String format = "| %-5s | %-22s | %-12s | %-12s | %-10s | %-10s |%n";
        String border = "+" + "-".repeat(7)
                + "+" + "-".repeat(24)
                + "+" + "-".repeat(14)
                + "+" + "-".repeat(14)
                + "+" + "-".repeat(12)
                + "+" + "-".repeat(12) + "+";

        System.out.println(border);
        System.out.printf(format,
                "ID", "Species", "Stage", "Zone", "Price", "Status");
        System.out.println(border);

        for (PlantBatch b : batches) {
            System.out.printf(format,
                    b.batchId(),
                    truncate(b.speciesName(), 22),
                    truncate(b.growthStage(), 12),
                    truncate(b.greenhouseZone(), 12),
                    money(b.price()),
                    b.status());
        }

        System.out.println(border);
    }
}