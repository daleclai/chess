package service;
import dataaccess.*;
import model.*;
import java.util.UUID;

public class UserService {
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    //register
    public RegResult register(RegRequest request) throws DataAccessException {
        if (request.username() == null || request.password() == null || request.email() == null) {
            throw new DataAccessException("Error: bad request");
        }
        if (dataAccess.getUser(request.username()) !=null) {
            throw new DataAccessException("Error: already taken");
        }
        UserData user = new UserData(request.username(), request.password(), request.email());
        dataAccess.createUser(user);
        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));
        return new RegResult(request.username(), token);
    }

    //login
    public LoginResult login(LoginReq request) throws DataAccessException {
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("Error: bad request");
        }
        UserData user = dataAccess.getUser(request.username());
        if (user == null || !user.password().equals(request.password())) {
            throw new DataAccessException("Error: unauthorized");
        }
        String token = UUID.randomUUID().toString();
        dataAccess.createAuth(new AuthData(token, request.username()));
        return new LoginResult(request.username(), token);
    }

    //logout
    public void logout(String authToken) throws DataAccessException {
        if (authToken == null || dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        dataAccess.deleteAuth(authToken);
    }
}
