package service.result;

public class LogoutResult {
    private String message;

    public LogoutResult() {}

    public LogoutResult(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}