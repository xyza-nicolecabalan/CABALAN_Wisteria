package com.wisteria;

import com.wisteria.dao.DatabaseInitializer;
import com.wisteria.dao.UserDao;
import com.wisteria.ui.Authentication;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Initialize the Wisteria database
        DatabaseInitializer.initialize();

        // Create one shared UserDao
        UserDao userDao = new UserDao();

        // Create one shared Scanner
        Scanner scanner = new Scanner(System.in);

        // Start authentication
        Authentication authentication =
                new Authentication(
                        userDao,
                        scanner
                );

        authentication.start();

        // Close scanner when the program exits
        scanner.close();
    }
}