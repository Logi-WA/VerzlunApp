package is.hi.hbv601g.verzlunapp.services.serviceimplementations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import is.hi.hbv601g.verzlunapp.services.NetworkService;

public class NetworkServiceImpl implements NetworkService {
    private static final String BASE_URL = "https://verzla-71cda7a37a2e.herokuapp.com";
    private static NetworkServiceImpl instance;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String authToken;

    private NetworkServiceImpl() {}

    public static synchronized NetworkServiceImpl getInstance() {
        if (instance == null) {
            instance = new NetworkServiceImpl();
        }
        return instance;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    private void addAuthHeader(HttpURLConnection connection) {
        if (authToken != null && !authToken.isEmpty()) {
            connection.setRequestProperty("Authorization", "Bearer " + authToken);
        } else {
            System.err.println("Auth token is null or empty");
        }
    }

    @Override
    public String sendRequest(String endpoint, String jsonPayload) {
        return post(endpoint, jsonPayload);
    }

    public boolean isAuthenticated() {
        String response = get("/api/users/me");
        return response != null;
    }

    @Override
    public String get(String endpoint) {
        try {
            Future<String> future = executor.submit(() -> {
                try {
                    String path = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
                    URL url = new URL(BASE_URL + path);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");

                    addAuthHeader(connection);

                    int responseCode = connection.getResponseCode();

                    if (responseCode == 401) {
                        System.err.println("GET request failed with code: 401 (Unauthorized)");
                        return null;
                    }

                    if (responseCode >= 200 && responseCode < 300) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                        }
                        return response.toString();
                    } else {
                        System.err.println("GET request failed with code: " + responseCode);
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String post(String endpoint, String jsonPayload) {
        try {
            Future<String> future = executor.submit(() -> {
                try {
                    String path = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
                    URL url = new URL(BASE_URL + path);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    addAuthHeader(connection);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();

                    if (responseCode >= 200 && responseCode < 300) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                        }
                        return response.toString();
                    } else {
                        System.err.println("POST request failed with code: " + responseCode);
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String patch(String endpoint, String jsonPayload) {
        try {
            Future<String> future = executor.submit(() -> {
                try {
                    String path = endpoint.startsWith("/") ? endpoint : "/" + endpoint;
                    URL url = new URL(BASE_URL + path);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("PATCH");
                    connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    addAuthHeader(connection);

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = connection.getResponseCode();

                    if (responseCode >= 200 && responseCode < 300) {
                        StringBuilder response = new StringBuilder();
                        try (BufferedReader br = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                        }
                        return response.toString();
                    } else {
                        System.err.println("PATCH request failed with code: " + responseCode);
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
