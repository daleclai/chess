package ui;


import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        serverUrl = "http://localhost:" + port;
    }

    public Object register(String username, String password, String email) throws Exception {
        var body = Map.of("username", username, "password", password, "email", email);
        return makeRequest("POST", "/user", body, Map.class);
    }

    public Object login(String username, String password) throws Exception {
        var body = Map.of("username", username, "password", password);
        return makeRequest("POST", "/session", body, Map.class);
    }

    public void logout(String authToken) throws Exception {
        makeRequest("DELETE", "/session", null, null, authToken);
    }

    public Object listGames(String authToken) throws Exception {
        return makeRequest("GET", "/game", null, Map.class, authToken);
    }

    public Object createGame(String authToken, String gameName) throws Exception {
        var body = Map.of("gameName", gameName);
        return makeRequest("POST", "/game", body, Map.class, authToken);
    }

    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var body = Map.of("gameID", gameID, "playerColor", playerColor);
        makeRequest("PUT", "/game", body, null, authToken);
    }

    private <T> T makeRequest(String method, String path, Object body, Class<T> responseClass) throws Exception {
        return makeRequest(method, path, body, responseClass, null);
    }

    private <T> T makeRequest(String method, String path, Object body, Class<T> responseClass, String authToken) throws Exception {
        URL url = new URI(serverUrl + path).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type", "application/json");

        if (authToken != null) {
            http.setRequestProperty("authorization", authToken);
        }

        if (body != null) {
            http.setDoOutput(true);
            try (var os = http.getOutputStream()) {
                os.write(gson.toJson(body).getBytes());
            }
        }

        http.connect();

        if (http.getResponseCode() / 100 != 2) {
            try (var is = http.getErrorStream()) {
                if (is != null) {
                    var error = new String(is.readAllBytes());
                    var map = gson.fromJson(error, Map.class);
                    throw new Exception((String) map.get("message"));
                }
            }
            throw new Exception("HTTP error: " + http.getResponseCode());
        }

        if (responseClass == null) {
            return null;
        }

        try (var is = http.getInputStream()) {
            return gson.fromJson(new String(is.readAllBytes()), responseClass);
        }
    }

    public void clear() throws Exception{
        makeRequest("DELETE", "/db", null, null);
    }

    public int getPort() {
        return Integer.parseInt(serverUrl.replace("http://localhost:", ""));
    }
}
