package com.wisteria.ui;

import com.wisteria.dao.UserDao;
import com.wisteria.model.User;

import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner;
    private final UserDao userDao;
    private final User user;

    public MainMenu(
            Scanner scanner,
            UserDao userDao,
            User user) {

        this.scanner = scanner;
        this.userDao = userDao;
        this.user = user;
    }


    // ==============================
    // SHOW MAIN MENU
    // ==============================

    public void show() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("          WISTERIA");
            System.out.println("==============================");

            System.out.println(
                    "Welcome, " + user.fullName()
            );

            System.out.println(
                    "Role: " + user.role()
            );

            System.out.println("==============================");

            switch (user.role().toUpperCase()) {

                case "CUSTOMER":
                    showCustomerMenu();
                    break;

                case "STAFF":
                    showStaffMenu();
                    break;

                case "ADMIN":
                    showAdminMenu();
                    break;

                default:
                    System.out.println(
                            "Unknown user role."
                    );
                    running = false;
                    continue;
            }

            System.out.println("0. Logout");

            System.out.println("==============================");

            System.out.print("Choose an option: ");

            String choice =
                    scanner.nextLine().trim();

            if (choice.equals("0")) {

                System.out.println();
                System.out.println(
                        "Logging out..."
                );

                running = false;

            } else {

                handleOption(choice);
            }
        }
    }


    // ==============================
    // CUSTOMER MENU
    // ==============================

    private void showCustomerMenu() {

        System.out.println(
                "1. Browse Plant Catalog"
        );

        System.out.println(
                "2. Check Batch Availability"
        );

        System.out.println(
                "3. Reserve / Purchase Plant"
        );

        System.out.println(
                "4. View Purchase History"
        );

        System.out.println(
                "5. View Profile"
        );
    }


    // ==============================
    // STAFF MENU
    // ==============================

    private void showStaffMenu() {

        System.out.println(
                "1. Manage Plant Batches"
        );

        System.out.println(
                "2. Log Daily Care Activity"
        );

        System.out.println(
                "3. Manage Health Incidents"
        );

        System.out.println(
                "4. Update Plant Condition"
        );

        System.out.println(
                "5. View Profile"
        );
    }


    // ==============================
    // ADMIN MENU
    // ==============================

    private void showAdminMenu() {

        System.out.println(
                "1. Manage Plant Inventory"
        );

        System.out.println(
                "2. Manage User Accounts"
        );

        System.out.println(
                "3. Manage Care Schedule Templates"
        );

        System.out.println(
                "4. View Growth & Mortality Reports"
        );

        System.out.println(
                "5. View Activity Logs"
        );

        System.out.println(
                "6. View Profile"
        );
    }


    // ==============================
    // HANDLE MENU OPTION
    // ==============================

    private void handleOption(String choice) {

        System.out.println();

        switch (user.role().toUpperCase()) {

            case "CUSTOMER":

                handleCustomerOption(choice);
                break;

            case "STAFF":

                handleStaffOption(choice);
                break;

            case "ADMIN":

                handleAdminOption(choice);
                break;

            default:

                System.out.println(
                        "Invalid user role."
                );
        }
    }


    // ==============================
    // CUSTOMER OPTIONS
    // ==============================

    private void handleCustomerOption(
            String choice) {

        switch (choice) {

            case "1":
                System.out.println(
                        "Plant Catalog will be implemented here."
                );
                break;

            case "2":
                System.out.println(
                        "Batch Availability will be implemented here."
                );
                break;

            case "3":
                System.out.println(
                        "Reservation and Purchase will be implemented here."
                );
                break;

            case "4":
                System.out.println(
                        "Purchase History will be implemented here."
                );
                break;

            case "5":
                viewProfile();
                break;

            default:
                System.out.println(
                        "Invalid option."
                );
        }
    }


    // ==============================
    // STAFF OPTIONS
    // ==============================

    private void handleStaffOption(
            String choice) {

        switch (choice) {

            case "1":
                System.out.println(
                        "Plant Batch Management will be implemented here."
                );
                break;

            case "2":
                System.out.println(
                        "Daily Care Logging will be implemented here."
                );
                break;

            case "3":
                System.out.println(
                        "Health Incident Management will be implemented here."
                );
                break;

            case "4":
                System.out.println(
                        "Plant Condition Updates will be implemented here."
                );
                break;

            case "5":
                viewProfile();
                break;

            default:
                System.out.println(
                        "Invalid option."
                );
        }
    }


    // ==============================
    // ADMIN OPTIONS
    // ==============================

    private void handleAdminOption(
            String choice) {

        switch (choice) {

            case "1":
                System.out.println(
                        "Plant Inventory Management will be implemented here."
                );
                break;

            case "2":
                System.out.println(
                        "User Account Management will be implemented here."
                );
                break;

            case "3":
                System.out.println(
                        "Care Schedule Templates will be implemented here."
                );
                break;

            case "4":
                System.out.println(
                        "Growth and Mortality Reports will be implemented here."
                );
                break;

            case "5":
                System.out.println(
                        "Activity Logs will be implemented here."
                );
                break;

            case "6":
                viewProfile();
                break;

            default:
                System.out.println(
                        "Invalid option."
                );
        }
    }


    // ==============================
    // VIEW PROFILE
    // ==============================

    private void viewProfile() {

        System.out.println();
        System.out.println("==============================");
        System.out.println("          MY PROFILE");
        System.out.println("==============================");

        System.out.println(
                "User ID: " + user.userId()
        );

        System.out.println(
                "Full Name: " + user.fullName()
        );

        System.out.println(
                "Email: " + user.email()
        );

        System.out.println(
                "Role: " + user.role()
        );

        System.out.println(
                "Created At: " + user.createdAt()
        );

        System.out.println("==============================");
    }
}