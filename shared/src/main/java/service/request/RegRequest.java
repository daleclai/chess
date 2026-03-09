package service.request;

public class RegRequest {
    private String username;
    private String password;
    private String email;

    public RegRequest() {}

    public RegRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }

    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setEmail(String email) { this.email = email; }

    public static class LoginRequest {
    }

    public static class RegisterRequest {
    }
}