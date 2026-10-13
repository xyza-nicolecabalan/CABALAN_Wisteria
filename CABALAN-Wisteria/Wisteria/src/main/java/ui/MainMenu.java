```java
        package com.wisteria.ui;

import com.wisteria.dao.UserDao;
import java.util.Scanner;

public class MainMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final UserDao userDao = new UserDao();

    private final RegisterMenu registerMenu =
            new RegisterMenu(scanner, userDao);

    private final LoginMenu loginMenu =
            new LoginMenu(scanner, userDao);

    public void show() {
        userDao.createTable();

        boolean running = true;

        while (running) {
            System.out.println("\n==============================");
            System.out.println("       WISTERIA SYSTEM");
            System.out.println("==============================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    registerMenu.show();
                    break;

                case "2":
                    loginMenu.show();
                    break;

                case "3":
                    System.out.println("Thank you for using Wisteria!");
                    running = false;
                    break;

                default:
                    System.out.println(
                            "Invalid choice. Please select 1-3.");
            }
        }

        scanner.close();
    }
}
```