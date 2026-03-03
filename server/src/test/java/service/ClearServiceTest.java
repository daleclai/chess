package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.request.RegRequest;

import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {

    private DataAccess dao;
    private ClearService clearService;
    private UserService userService;

    @BeforeEach
    void setUp() {
        dao = new MemoryDataAccess();
        clearService = new ClearService(dao);
        userService = new UserService(dao);
    }

    @Test
    void clearPositive() throws DataAccessException {

        userService.register(
                new RegRequest("bob", "pass", "email"));

        assertDoesNotThrow(() -> clearService.clear());
    }
}