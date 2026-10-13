```java
        package com.wisteria.ui;

import com.wisteria.dao.UserDao;
import java.util.Scanner;

public class LoginMenu {

    private final Scanner scanner;
    private final UserDao userDao;

    public LoginMenu(Scanner scanner, UserDao userDao) {
        this.scanner = scanner;
        this.userDao = userDao;
    }

    public boolean show() {
        System.out.println("\n===== WISTERIA LOGIN =====");

        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        boolean success = userDao.login(username, password);

        if (success) {
            System.out.println("Login successful!");
            System.out.println("Welcome, " + username + "!");
            return true;
        } else {
            System.out.println("Login failed.");
            return false;
        }
    }
}
```