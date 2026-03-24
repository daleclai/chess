package client;

import ui.Repl;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        new Repl(8080).run();
    }
}