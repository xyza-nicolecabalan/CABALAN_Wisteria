package com.wisteria.ui;

import com.wisteria.dao.ActivityLogDao;
import com.wisteria.dao.UserDao;
import com.wisteria.model.ActivityLog;
import com.wisteria.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private static final List<String> VALID_ROLES =
            List.of("CUSTOMER", "STAFF", "ADMIN");

    private static final List<String> VALID_STATUSES =
            List.of("APPROVED", "PENDING");

    private final Scanner scanner;
    private final UserDao userDao;
    private final User admin;
    private final ActivityLogDao activityLogDao = new ActivityLogDao();

    public AdminMenu(Scanner scanner, UserDao userDao, User admin) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.admin = admin;
    }

    // SHOW ADMIN MENU

    public void show() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("          ADMIN MENU");
            System.out.println("==============================");
            System.out.println("Welcome, " + admin.fullName());
            System.out.println("==============================");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role & Status");
            System.out.println("5. View System Audit Logs");
            System.out.println("0. Logout");
            System.out.println("==============================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    viewPendingUsers();
                    break;
                case "3":
                    approveUser();
                    break;
                case "4":
                    updateUser();
                    break;
                case "5":
                    auditLogMenu();
                    break;
                case "0":
                    System.out.println();
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println();
                    System.out.println("Invalid option.");
            }
        }
    }

    // VIEW ALL USERS

    private void viewAllUsers() {

        System.out.println();
        System.out.println("---------- ALL SYSTEM USERS ----------");

        printUserTable(userDao.getAllUsers());
    }

    // VIEW PENDING USERS

    private void viewPendingUsers() {

        System.out.println();
        System.out.println("------- PENDING REGISTRATIONS -------");

        printUserTable(userDao.getPendingUsers());
    }

    // APPROVE USER

    private void approveUser() {

        List<User> pending = userDao.getPendingUsers();

        System.out.println();
        System.out.println("---------- APPROVE USER ----------");

        if (pending.isEmpty()) {
            System.out.println("No pending accounts to approve.");
            return;
        }

        printUserTable(pending);

        while (true) {

            System.out.print(
                    "Enter User ID to approve (0 to cancel): ");

            String input = scanner.nextLine().trim();

            // Cancel
            if (input.equals("0")) {
                System.out.println("Approval cancelled.");
                return;
            }

            // Must be a number
            int targetId;
            try {
                targetId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            // Must be in the pending list
            boolean inPending = pending.stream()
                    .anyMatch(u -> u.userId() == targetId);

            if (!inPending) {
                System.out.println(
                        "User ID " + targetId
                                + " is not in the pending list.");
                continue;
            }

            // Approve (also writes the USER_APPROVED audit log)
            if (userDao.approveUser(admin.userId(), targetId)) {
                System.out.println(
                        "User ID " + targetId + " has been APPROVED.");
            } else {
                System.out.println("Approval failed.");
            }
            return;
        }
    }

    // UPDATE USER ROLE & STATUS

    private void updateUser() {

        System.out.println();
        System.out.println("------- UPDATE USER ROLE & STATUS -------");

        List<User> users = userDao.getAllUsers();
        printUserTable(users);

        // ---- Step A: choose the target user ----
        User target = null;

        while (target == null) {

            int targetId = readIntInput(
                    "Enter User ID to update (0 to cancel): ");

            // Cancel
            if (targetId == 0) {
                System.out.println("Update cancelled.");
                return;
            }

            // Self-edit restriction
            if (targetId == admin.userId()) {
                System.out.println(
                        "WARNING: Modifying the currently active admin account is restricted.");
                continue;
            }

            // Find the user in the list
            for (User u : users) {
                if (u.userId() == targetId) {
                    target = u;
                    break;
                }
            }

            if (target == null) {
                System.out.println(
                        "User ID " + targetId + " does not exist.");
            }
        }

        // ---- Step B: choose the new role ----
        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // ---- Step C: choose the new status ----
        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        // ---- Step D: save to the database (also writes USER_UPDATED log) ----
        if (userDao.updateUserRoleAndStatus(
                admin.userId(), target.userId(), newRole, newStatus)) {

            System.out.println();
            System.out.println("SUCCESS: " + target.fullName()
                    + " (ID " + target.userId() + ") updated.");
            System.out.println("New Role:   " + newRole);
            System.out.println("New Status: " + newStatus);
        } else {
            System.out.println("Update failed.");
        }
    }

    // INPUT VALIDATION WRAPPERS

    // Keeps asking until the user types a whole number. Never crashes.
    private int readIntInput(String prompt) {

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

    // Returns a valid role, or null if the admin typed 0 to cancel.
    private String readValidRole() {

        while (true) {

            System.out.print(
                    "Enter new role (CUSTOMER / STAFF / ADMIN, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                return null;
            }

            if (VALID_ROLES.contains(input)) {
                return input;
            }

            System.out.println(
                    "Invalid role. Choose CUSTOMER, STAFF, or ADMIN.");
        }
    }

    // Returns a valid status, or null if the admin typed 0 to cancel.
    private String readValidStatus() {

        while (true) {

            System.out.print(
                    "Enter new status (APPROVED / PENDING, 0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.equals("0")) {
                return null;
            }

            if (VALID_STATUSES.contains(input)) {
                return input;
            }

            System.out.println(
                    "Invalid status. Choose APPROVED or PENDING.");
        }
    }

    // AUDIT LOG SUBMENU

    private void auditLogMenu() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("      SYSTEM AUDIT LOGS");
            System.out.println("==============================");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Clear All Logs");
            System.out.println("0. Back");
            System.out.println("==============================");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println();
                    System.out.println("---------- ALL SYSTEM LOGS ----------");
                    printLogTable(activityLogDao.getAllLogs());
                    break;
                case "2":
                    filterLogsByUser();
                    break;
                case "3":
                    clearLogs();
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

    // FILTER LOGS BY USER ID

    private void filterLogsByUser() {

        int userId = readIntInput("Enter User ID to filter (0 to cancel): ");

        if (userId == 0) {
            System.out.println("Filter cancelled.");
            return;
        }

        System.out.println();
        System.out.println("------- LOGS FOR USER ID " + userId + " -------");
        printLogTable(activityLogDao.getLogsByUserId(userId));
    }

    // CLEAR LOGS (WITH CONFIRMATION)

    private void clearLogs() {

        System.out.println();
        System.out.println("WARNING: This permanently deletes ALL activity logs.");
        System.out.print("Type YES to confirm (anything else cancels): ");

        String confirm = scanner.nextLine().trim();

        if (!confirm.equals("YES")) {
            System.out.println("Clear cancelled. No logs were deleted.");
            return;
        }

        if (activityLogDao.clearAllLogs()) {
            System.out.println("All activity logs have been cleared.");

            // Leave a record that the clear happened
            activityLogDao.logAction(
                    admin.userId(),
                    "LOGS_CLEARED",
                    "Admin " + admin.userId() + " cleared all activity logs.");
        } else {
            System.out.println("Failed to clear logs.");
        }
    }

    // LOG TABLE RENDERING

    private void printLogTable(List<ActivityLog> logs) {

        if (logs.isEmpty()) {
            System.out.println("No logs found.");
            return;
        }

        String format = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        String border = "-".repeat(98);

        System.out.println(border);
        System.out.printf(format,
                "ID", "User ID", "Action", "Description", "Timestamp");
        System.out.println(border);

        for (ActivityLog log : logs) {

            String desc = log.description();
            int pos = 0;
            boolean firstLine = true;

            // Long descriptions wrap onto extra lines so nothing is cut off
            do {
                String chunk = desc.substring(
                        pos, Math.min(pos + 35, desc.length()));

                if (firstLine) {
                    System.out.printf(format,
                            log.logId(),
                            log.userId(),
                            log.actionType(),
                            chunk,
                            log.timestamp());
                } else {
                    System.out.printf(format, "", "", "", chunk, "");
                }

                pos += 35;
                firstLine = false;

            } while (pos < desc.length());
        }

        System.out.println(border);
    }

    // USER TABLE RENDERING HELPER

    private void printUserTable(List<User> users) {

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        String format = "| %-5s | %-15s | %-12s | %-10s |%n";
        String border = "+-------+-----------------+--------------+------------+";

        System.out.println(border);
        System.out.printf(format, "ID", "Name", "Role", "Status");
        System.out.println(border);

        for (User u : users) {
            System.out.printf(format,
                    u.userId(),
                    truncate(u.fullName(), 15),
                    u.role(),
                    u.status());
        }

        System.out.println(border);
    }

    // Keeps long names from breaking the table alignment
    private String truncate(String text, int max) {
        if (text == null) return "";
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
