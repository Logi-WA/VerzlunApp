package is.hi.hbv601g.verzlunapp.persistence;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String userId;
    private String name;
    private String email;
    private String password;

    public User(String userId, String name, String email, String password) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Create a User from JSON
    public static User fromJson(JSONObject json) {
        try {
            // Assuming userId is also in JSON
            String id = json.optString("userId", null); // Or appropriate key
            return new User(
                    id,
                    json.getString("name"),
                    json.getString("email"),
                    json.optString("password", "")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Getters
    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    // Setters
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("userId", userId);
            json.put("name", name);
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}