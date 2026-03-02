package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import service.request.LoginRequest;
import service.request.RegRequest;
import service.result.AuthData;
import service.result.LogoutResult;
import service.result.RegResult;

import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegResult register(RegRequest request) throws DataAccessException {

        if (request.getUsername() == null ||
                request.getPassword() == null ||
                request.getEmail() == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (dataAccess.getUser(request.getUsername()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        UserData user = new UserData(
                request.getUsername(),
                request.getPassword(),
                request.getEmail());

        dataAccess.createUser(user);

        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new model.AuthData(token, request.getUsername()));

        return new RegResult(request.getUsername(), token);
    }

    public AuthData login(LoginRequest request) throws DataAccessException {

        UserData user = dataAccess.getUser(request.getUsername());

        if (user == null ||
                !user.password().equals(request.getPassword())) {
            throw new DataAccessException("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new model.AuthData(token, request.getUsername()));

        return new AuthData(token, request.getUsername());
    }

    public LogoutResult logout(String authToken) throws DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Invalid or missing auth token");
        }

        dataAccess.deleteAuth(authToken); // remove the token from DB
        return new LogoutResult("Successfully logged out");
    }
}