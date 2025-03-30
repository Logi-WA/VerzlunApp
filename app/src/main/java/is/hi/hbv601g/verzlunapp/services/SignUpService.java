package is.hi.hbv601g.verzlunapp.services;

import org.json.JSONException;
import org.json.JSONObject;

import is.hi.hbv601g.verzlunapp.persistence.User;

public class SignUpService {
    private final NetworkService networkService;
    private final UserService userService;

    public SignUpService(NetworkService networkService, UserService userService) {
        this.networkService = networkService;
        this.userService = userService;
    }

    public boolean registerUser(User user) {
        try {
            // Create user payload
            JSONObject userPayload = new JSONObject();
            userPayload.put("name", user.getName());
            userPayload.put("email", user.getEmail());
            userPayload.put("password", user.getPassword());

            // Send registration request
            String response = networkService.post("/api/users", userPayload.toString());

            // If we got a response, assume registration was successful
            if (response != null) {
                userService.setCurrentUser(user);
                return true;
            }
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
