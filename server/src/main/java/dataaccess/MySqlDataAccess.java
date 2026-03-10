package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlDataAccess {
    private final Gson gson = new Gson();
    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS user (
                username VARCHAR(256) PRIMARY KEY,
                hashed_password VARCHAR(256) NOT NULL,
                email VARCHAR(256) NOT NULL
                )
            """,
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(256) PRIMARY KEY,
                username VARCHAR(256) NOT NULL
                )
            """,

            """
            CREATE TABLE IF NOT EXISTS game (
                gameID INT AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(256),
                blackUsername VARCHAR(256),
                gameName VARCHAR(256) NOT NULL,
                game_state TEXT NOT NULL
                )
            """
    };
}
