package is.hi.hbv601g.verzlunapp.persistence;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private String name;
    private String email;
    private String password;

    // Constructor
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Convert to JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("email", email);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Create a User from JSON
    public static User fromJson(JSONObject json) {
        try {
            return new User(
                    json.getString("name"),
                    json.getString("email"),
                    json.getString("password")
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
