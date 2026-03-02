package service.result;

public class AuthData {
    private String authToken;
    private String username;

    public AuthData() {}

    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() { return authToken; }
    public String getUsername() { return username; }

    public void setAuthToken(String authToken) { this.authToken = authToken; }
    public void setUsername(String username) { this.username = username; }
}