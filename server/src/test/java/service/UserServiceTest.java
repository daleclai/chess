package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MySqlDataAccess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.LoginRequest;
import service.request.RegRequest;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private DataAccess dataAccess;

    @BeforeEach
    void setUp() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        dataAccess.clear();
        userService = new UserService(dataAccess);
    }

    // register

    @Test
    void registerPositive() throws DataAccessException {
        RegRequest request = new RegRequest("bob", "password", "bob@email.com");

        var result = userService.register(request);

        assertNotNull(result);
        assertEquals("bob", result.getUsername());
        assertNotNull(result.getAuthToken());
    }

    @Test
    void registerNegativeAlreadyTaken() throws DataAccessException {
        RegRequest request = new RegRequest("bob", "password", "email");

        userService.register(request);

        assertThrows(DataAccessException.class, () -> {
            userService.register(request);
        });
    }

    // login

    @Test
    void loginPositive() throws DataAccessException {
        userService.register(
                new RegRequest("alice", "pass", "email"));

        LoginRequest login = new LoginRequest("alice", "pass");

        var result = userService.login(login);

        assertNotNull(result.getAuthToken());
        assertEquals("alice", result.getUsername());
    }

    @Test
    void loginNegativeWrongPassword() throws DataAccessException {
        userService.register(
                new RegRequest("alice", "pass", "email"));

        LoginRequest badLogin = new LoginRequest("alice", "wrong");

        assertThrows(DataAccessException.class, () -> {
            userService.login(badLogin);
        });
    }

    // logout

    @Test
    void logoutPositive() throws DataAccessException {
        var reg = userService.register(
                new RegRequest("tom", "pass", "email"));

        assertDoesNotThrow(() ->
                userService.logout(reg.getAuthToken()));
    }

    @Test
    void logoutNegativeInvalidToken() {
        assertThrows(DataAccessException.class, () -> {
            userService.logout("badToken");
        });
    }
}