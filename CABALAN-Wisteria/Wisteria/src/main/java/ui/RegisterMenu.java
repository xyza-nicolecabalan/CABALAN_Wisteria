```java
        package com.wisteria.ui;

import com.wisteria.dao.UserDao;
import java.util.Scanner;

public class RegisterMenu {

    private final Scanner scanner;
    private final UserDao userDao;

    public RegisterMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public void show() {
        System.out.println("\n===== WISTERIA REGISTRATION =====");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Username and password are required.");
            return;
        }

        System.out.println("\nSelect your role:");
        System.out.println("1. Customer");
        System.out.println("2. Nursery Technician");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();
        String role;

        switch (choice) {
            case "1":
                role = "CUSTOMER";
                break;
            case "2":
                role = "TECHNICIAN";
                break;
            default:
                System.out.println("Invalid role selection.");
                return;
        }

        boolean success = userDao.register(username, password, role);

        if (success) {
            System.out.println("Registration successful!");
            System.out.println("Your account is PENDING approval.");
        } else {
            System.out.println(
                    "Could not register. The username may already exist.");
        }
    }
}
```