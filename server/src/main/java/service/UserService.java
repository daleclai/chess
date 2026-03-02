package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.request.RegRequest;
import service.result.RegResult;
import java.util.UUID;

public class UserService {

    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegResult register(RegRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getUser(request.username()) != null) {
            throw new DataAccessException("Error: already taken");
        }

        UserData user = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(user);

        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));

        return new RegResult(request.username(), token);
    }
}