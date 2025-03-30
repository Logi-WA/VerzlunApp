package is.hi.hbv601g.verzlunapp.services;

import org.json.JSONException;
import org.json.JSONObject;

import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;

public class SignInService {
    private final NetworkService networkService;
    private final UserService userService;

    public SignInService(NetworkService networkService, UserService userService) {
        this.networkService = networkService;
        this.userService = userService;
    }

    public boolean loginUser(String email, String password) {
        try {
            JSONObject loginPayload = new JSONObject();
            loginPayload.put("username", email);
            loginPayload.put("password", password);

            String response = networkService.post("/auth/login", loginPayload.toString());

            if (response != null) {
                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");

                if (success) {
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String token = data.getString("token");

                    // Store the token in both NetworkServiceImpl and RetrofitClient
                    NetworkServiceImpl.getInstance().setAuthToken(token);
                    RetrofitClient.INSTANCE.setAuthToken(token);

                    // Create user and save
                    String name = data.getString("name");
                    String userEmail = data.getString("email");
                    String userId = data.getString("userId");
                    User user = new User(name, userEmail, "");
                    userService.setCurrentUser(user);

                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}