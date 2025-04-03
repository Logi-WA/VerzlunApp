package is.hi.hbv601g.verzlunapp.services;

import java.io.IOException;

import is.hi.hbv601g.verzlunapp.model.LoginRequest;
import is.hi.hbv601g.verzlunapp.model.LoginResponse;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;
import retrofit2.Response;

public class SignInService {
    private final UserService userService;
    private final ApiService apiService;

    // Updated Constructor
    public SignInService(UserService userService) {
        this.userService = userService;
        this.apiService = RetrofitClient.INSTANCE.getApiService();
    }

    public boolean loginUser(String email, String password) {
        // Create Kotlin LoginRequest object
        LoginRequest loginRequest = new LoginRequest(email, password);

        try {
            // Execute the network request synchronously
            Response<LoginResponse> response = apiService.login(loginRequest).execute();

            // Check if the call was successful and response body is not null
            if (response.isSuccessful() && response.body() != null) {
                LoginResponse loginResponse = response.body();

                // Check the 'success' flag within the response body
                if (loginResponse.getSuccess()) {
                    // Extract data from the nested 'data' object
                    String token = loginResponse.getData().getToken();
                    String userId = loginResponse.getData().getUserId();
                    String name = loginResponse.getData().getName();
                    String userEmail = loginResponse.getData().getEmail();

                    // Store the token ONLY in RetrofitClient
                    RetrofitClient.INSTANCE.setAuthToken(token);

                    // Create and store the local User object
                    User user = new User(userId, name, userEmail, "");
                    userService.setCurrentUser(user);

                    return true;
                } else {
                    // API call succeeded but login failed
                    System.err.println("Login failed: " + loginResponse.getMessage());
                    return false;
                }
            } else {
                // Handle unsuccessful HTTP responses
                String errorMsg = "Login failed with HTTP status: " + response.code();
                if (response.errorBody() != null) {
                    try {
                        errorMsg += " - " + response.errorBody().string();
                    } catch (IOException e) {
                    }
                }
                System.err.println(errorMsg);
                return false;
            }
        } catch (IOException e) {
            // Handle network errors
            System.err.println("Network error during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            // Handle other unexpected errors
            System.err.println("Unexpected error during login: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}