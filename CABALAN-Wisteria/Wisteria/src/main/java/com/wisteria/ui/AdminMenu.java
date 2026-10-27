package com.wisteria.ui;

import com.wisteria.dao.UserDao;
import com.wisteria.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {

    private final Scanner scanner;
    private final UserDao userDao;
    private final User admin;

    public AdminMenu(Scanner scanner, UserDao userDao, User admin) {
        this.scanner = scanner;
        this.userDao = userDao;
        this.admin = admin;
    }

    // ==============================
    // SHOW ADMIN MENU
    // ==============================

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

    // ==============================
    // VIEW ALL USERS
    // ==============================

    private void viewAllUsers() {

        System.out.println();
        System.out.println("---------- ALL SYSTEM USERS ----------");

        printUserTable(userDao.getAllUsers());
    }

    // ==============================
    // VIEW PENDING USERS
    // ==============================

    private void viewPendingUsers() {

        System.out.println();
        System.out.println("------- PENDING REGISTRATIONS -------");

        printUserTable(userDao.getPendingUsers());
    }

    // ==============================
    // APPROVE USER
    // ==============================

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

            // Approve
            if (userDao.approveUser(targetId)) {
                System.out.println(
                        "User ID " + targetId + " has been APPROVED.");
            } else {
                System.out.println("Approval failed.");
            }
            return;
        }
    }

    // ==============================
    // TABLE RENDERING HELPER
    // ==============================

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