package service.result;

public record RegResult(String username, String token) {
    public RegResult(String errorMessage) {
        this(null, errorMessage);
    }
}