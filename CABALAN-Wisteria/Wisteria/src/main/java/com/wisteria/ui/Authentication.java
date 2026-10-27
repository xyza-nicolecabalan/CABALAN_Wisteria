package com.wisteria.ui;

import com.wisteria.dao.UserDao;
import com.wisteria.model.User;

import java.util.Scanner;

public class Authentication {

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(
            UserDao userDao,
            Scanner scanner) {

        this.userDao = userDao;
        this.scanner = scanner;
    }


    // ==============================
    // START AUTHENTICATION SYSTEM
    // ==============================

    public void start() {

        boolean running = true;

        while (running) {

            System.out.println();
            System.out.println("==============================");
            System.out.println("          WISTERIA");
            System.out.println("==============================");
            System.out.println(
                    "Rare Plant Care &"
            );
            System.out.println(
                    "Seedling Micro-Nursery Log"
            );
            System.out.println("==============================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.println("==============================");

            System.out.print("Choose an option: ");

            String choice =
                    scanner.nextLine().trim();

            switch (choice) {

                case "1":
                    register();
                    break;

                case "2":
                    login();
                    break;

                case "3":
                    System.out.println();
                    System.out.println(
                            "Thank you for using Wisteria!"
                    );
                    running = false;
                    break;

                default:
                    System.out.println();
                    System.out.println(
                            "Invalid choice. Please select 1-3."
                    );
            }
        }
    }


    // ==============================
    // REGISTER
    // ==============================

    private void register() {

        System.out.println();
        System.out.println("==============================");
        System.out.println("       REGISTER ACCOUNT");
        System.out.println("==============================");

        System.out.print("Full name: ");
        String fullName =
                scanner.nextLine().trim();

        System.out.print("Email: ");
        String email =
                scanner.nextLine().trim();

        System.out.print("Password: ");
        String password =
                scanner.nextLine().trim();

        System.out.println();
        System.out.println("Select your role:");
        System.out.println("1. Customer/Buyer");
        System.out.println("2. Nursery Technician/Staff");

        System.out.print("Choose role: ");

        String roleChoice =
                scanner.nextLine().trim();

        String role;

        switch (roleChoice) {

            case "1":
                role = "CUSTOMER";
                break;

            case "2":
                role = "STAFF";
                break;

            default:
                System.out.println();
                System.out.println(
                        "Invalid role selection."
                );
                return;
        }

        if (fullName.trim().isEmpty()
                || email.trim().isEmpty()
                || password.trim().isEmpty()) {

            System.out.println();
            System.out.println(
                    "All fields are required."
            );

            return;
        }

        boolean registered =
                userDao.registerUser(
                        fullName,
                        email,
                        password,
                        role
                );

        if (registered) {

            System.out.println();
            System.out.println(
                    "Registration successful!"
            );
            System.out.println(
                    "You can now log in."
            );
        }
    }


    // ==============================
    // LOGIN
    // ==============================

    private void login() {

        System.out.println();
        System.out.println("==============================");
        System.out.println("            LOGIN");
        System.out.println("==============================");

        System.out.print("Email: ");
        String email =
                scanner.nextLine().trim();

        System.out.print("Password: ");
        String password =
                scanner.nextLine().trim();

        User user =
                userDao.login(
                        email,
                        password
                );

        if (user == null) {

            System.out.println();
            System.out.println(
                    "Invalid email or password."
            );

            return;
        }

        System.out.println();
        System.out.println("==============================");
        System.out.println("       LOGIN SUCCESSFUL");
        System.out.println("==============================");

        System.out.println(
                "Welcome, " + user.fullName() + "!"
        );

        System.out.println(
                "Role: " + user.role()
        );

        routeUser(user);
    }


    // ==============================
    // ROLE ROUTING
    // ==============================

    private void routeUser(User user) {

        switch (user.role().toUpperCase()) {

            case "CUSTOMER":

                System.out.println();
                System.out.println(
                        "Opening Customer Menu..."
                );

                openMainMenu(user);
                break;


            case "STAFF":

                System.out.println();
                System.out.println(
                        "Opening Nursery Staff Menu..."
                );

                openMainMenu(user);
                break;


            case "ADMIN":

                System.out.println();
                System.out.println(
                        "Opening Admin Menu..."
                );

                openMainMenu(user);
                break;


            default:

                System.out.println();
                System.out.println(
                        "Unknown role: "
                                + user.role()
                );
        }
    }


    // ==============================
    // OPEN MAIN MENU
    // ==============================

    private void openMainMenu(User user) {

        MainMenu mainMenu =
                new MainMenu(
                        scanner,
                        userDao,
                        user
                );

        mainMenu.show();
    }
}