package is.hi.hbv601g.verzlunapp.services;

import android.util.Log;

import java.io.IOException;

import is.hi.hbv601g.verzlunapp.model.LoginRequest;
import is.hi.hbv601g.verzlunapp.model.LoginResponse;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;

public class SignInService {

    private final ApiService apiService;
    private static final String TAG = "SignInService";

    public SignInService() {

        this.apiService = RetrofitClient.INSTANCE.getApiService();
    }

    /**
     * Attempts to log in the user via the API.
     *
     * @param email    User's email.
     * @param password User's password.
     * @return The logged-in User object with token set if successful, null otherwise.
     */
    public User loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        Log.d(TAG, "Attempting login for: " + email);

        try {

            retrofit2.Response<LoginResponse> response = apiService.login(loginRequest).execute();

            if (response.isSuccessful() && response.body() != null) {
                LoginResponse loginResponse = response.body();

                if (loginResponse.getData() != null && loginResponse.getData().getToken() != null) {
                    String token = loginResponse.getData().getToken();
                    String userId = loginResponse.getData().getUserId();
                    String name = loginResponse.getData().getName();
                    String userEmail = loginResponse.getData().getEmail();

                    User user = new User(userId, name, userEmail, "");
                    user.setAuthToken(token);

                    Log.i(TAG, "Login successful for: " + userEmail);

                    return user;

                } else {
                    Log.e(TAG, "Login response successful, but token or user data missing in response body.");
                    return null;
                }

            } else {

                String errorMsg = "Login failed with HTTP status: " + response.code();
                if (response.errorBody() != null) {
                    try {
                        errorMsg += " - " + response.errorBody().string();
                    } catch (IOException ignored) {}
                }
                Log.e(TAG, errorMsg);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Network error during login: " + e.getMessage(), e);
            return null;
        } catch (Exception e) {

            Log.e(TAG, "Unexpected error during login: " + e.getMessage(), e);
            return null;
        }
    }
}