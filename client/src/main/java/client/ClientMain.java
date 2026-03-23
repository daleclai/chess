package client;

import ui.ServerFacade;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        System.out.println("♕ Welcome to 240 Chess Client!");
        System.out.println("Type 'help' to get started.");

        Scanner scanner = new Scanner(System.in);
        String input = "";

        String authToken = null;
        boolean loggedIn = false;

        ServerFacade facade = new ServerFacade(8080);
        while (!input.equalsIgnoreCase("quit")) {
            System.out.print(">>> ");
            input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase();

            try {
                if (!loggedIn) {
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

                            Map<String, Object> regAuth =
                                    (Map<String, Object>) facade.register(tokens[1], tokens[2], tokens[3]);

                            authToken = (String) regAuth.get("authToken");
                            loggedIn = true;

                            System.out.println("Successfully registered and logged in!");
                            break;

                        case "login":
                            if (tokens.length != 3) {
                                System.out.println("Usage: login <username> <password>");
                                break;
                            }

                            Map<String, Object> loginAuth =
                                    (Map<String, Object>) facade.login(tokens[1], tokens[2]);

                            authToken = (String) loginAuth.get("authToken");
                            loggedIn = true;

                            System.out.println("Successfully logged in!");
                            break;

                        case "quit":
                            System.out.println("Goodbye!");
                            break;

                        default:
                            System.out.println("Unknown command. Type 'help'.");
                    }
                }

                else {
                    switch (command) {
                        case "help":
                            System.out.println("Commands:");
                            System.out.println("  create <gameName>");
                            System.out.println("  list");
                            System.out.println("  logout");
                            System.out.println("  quit");
                            break;

                        case "create":
                            if (tokens.length < 2) {
                                System.out.println("Usage: create <gameName>");
                                break;
                            }

                            String gameName = input.substring(input.indexOf(" ") + 1);
                            facade.createGame(authToken, gameName);

                            System.out.println("Game created!");
                            break;

                        case "list":
                            Map<String, Object> response =
                                    (Map<String, Object>) facade.listGames(authToken);

                            List<Map<String, Object>> games =
                                    (List<Map<String, Object>>) response.get("games");

                            if (games.isEmpty()) {
                                System.out.println("No games available.");
                            } else {
                                int i = 1;
                                for (Map<String, Object> game : games) {
                                    System.out.println(i++ + ". " + game.get("gameName"));
                                }
                            }
                            break;

                        case "logout":
                            facade.logout(authToken);
                            authToken = null;
                            loggedIn = false;

                            System.out.println("Logged out.");
                            break;

                        case "quit":
                            System.out.println("Goodbye!");
                            break;

                        default:
                            System.out.println("Unknown command. Type 'help'.");
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}