package is.hi.hbv601g.verzlunapp.services;

import java.io.IOException;

import is.hi.hbv601g.verzlunapp.model.LoginRequest;
import is.hi.hbv601g.verzlunapp.model.LoginResponse;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;

public class SignInService {
    private final UserService userService;
    private final ApiService apiService;

    public SignInService(UserService userService) {
        this.userService = userService;
        this.apiService = RetrofitClient.INSTANCE.getApiService();
    }

    public boolean loginUser(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        try {
            retrofit2.Response<LoginResponse> response = apiService.login(loginRequest).execute();

            if (response.isSuccessful() && response.body() != null) {
                LoginResponse loginResponse = response.body();

                if (loginResponse.getSuccess()) {
                    String token = loginResponse.getData().getToken();
                    String userId = loginResponse.getData().getUserId();
                    String name = loginResponse.getData().getName();
                    String userEmail = loginResponse.getData().getEmail();

                    RetrofitClient.INSTANCE.setAuthToken(token);
                    NetworkServiceImpl.getInstance().setAuthToken(token);

                    User user = new User(userId, name, userEmail, "");
                    userService.setCurrentUser(user);

                    return true;
                } else {
                    System.err.println("Login failed: " + loginResponse.getMessage());
                    return false;
                }
            } else {
                String errorMsg = "Login failed with HTTP status: " + response.code();
                if (response.errorBody() != null) {
                    try {
                        errorMsg += " - " + response.errorBody().string();
                    } catch (IOException ignored) {}
                }
                System.err.println(errorMsg);
                return false;
            }
        } catch (IOException e) {
            System.err.println("Network error during login: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error during login: " + e.getMessage());
            return false;
        }
    }
}
