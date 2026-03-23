package client;

import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess Client!");
        System.out.println("Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equals("quit")) {
            System.out.print(">>> ");
            input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] tokens = input.split(" ");
            String command = tokens[0].toLowerCase();

            switch (command) {
                case "help":
                    System.out.println("Commands:");
                    System.out.println("  register <username> <password> <email>");
                    System.out.println("  login <username> <password>");
                    System.out.println("  quit");
                    break;

                case "register":
                    if (tokens.length != 4) {
                        System.out.println("Usage: register <username> <password> <email>");
                        break;
                    }

                    System.out.println("Registering " + tokens[1]);
                    break;

                case "login":
                    if (tokens.length != 3) {
                        System.out.println("Usage: login <username> <password>");
                        break;
                    }

                    System.out.println("Logging in " + tokens[1]);
                    break;

                case "quit":
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Unknown command. Type 'help'.");
            }
        }
    }
}