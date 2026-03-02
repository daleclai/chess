package service.request;

public class LogoutRequest {
    private String authToken;

    public LogoutRequest() {} // for Jackson

    public LogoutRequest(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }
}