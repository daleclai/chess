package service.result;

public class RegResult {
    private String username;
    private String authToken;

    public RegResult() {}

    public RegResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() { return username; }
    public String getAuthToken() { return authToken; }

    public void setUsername(String username) { this.username = username; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }
}