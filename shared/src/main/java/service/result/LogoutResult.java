package service.result;

public class LogoutResult {
    private String message;

    public LogoutResult() {} // default for Jackson

    public LogoutResult(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
}