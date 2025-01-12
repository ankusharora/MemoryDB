package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import org.box.model.InMemoryDatabase;
import org.box.model.TransactionManager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InMemoryDatabase database = new InMemoryDatabase();
        TransactionManager transactionManager = database.getTransactionManager();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the In-Memory Database. Enter commands:");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] parts = input.split("\\s+");

            if (parts.length == 0) continue;

            String command = parts[0].toUpperCase();

            try {
                switch (command) {
                    case "GET":
                        if (parts.length != 2) {
                            System.out.println("Usage: GET <key>");
                            break;
                        }
                        String value = database.get(parts[1]);
                        System.out.println(value != null ? value : "NULL");
                        break;

                    case "SET":
                        if (parts.length != 3) {
                            System.out.println("Usage: SET <key> <value>");
                            break;
                        }
                        database.set(parts[1], parts[2]);
                        System.out.println("OK");
                        break;

                    case "GETCOUNT_OF_VALUES":
                        if (parts.length != 2) {
                            System.out.println("Usage: GETCOUNT_OF_VALUES <value>");
                            break;
                        }
                        int count = database.getCountOfValue(parts[1]);
                        System.out.println(count);
                        break;

                    case "BEGIN":
                        transactionManager.begin();
                        System.out.println("OK");
                        break;

                    case "COMMIT":
                        transactionManager.commit();
                        System.out.println("OK");
                        break;

                    case "ROLLBACK":
                        transactionManager.rollback();
                        System.out.println("OK");
                        break;

                    case "EXIT":
                        System.out.println("Exiting...");
                        scanner.close();
                        return;

                    default:
                        System.out.println("Unknown command: " + command);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
