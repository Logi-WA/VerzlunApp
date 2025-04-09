package is.hi.hbv601g.verzlunapp.persistence;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserStorage {
    private static final String PREFS_NAME = "user_prefs";
    private static final String USERS_KEY = "users";

    private static final String LOGGED_IN_USER_ID_KEY = "logged_in_user_id";
    private static final String LOGGED_IN_USER_NAME_KEY = "logged_in_user_name";
    private static final String LOGGED_IN_USER_EMAIL_KEY = "logged_in_user_email";

    private SharedPreferences sharedPreferences;

    public UserStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save user to SharedPreferences
    public void saveUser(User user) {
        List<User> users = getUsers();
        users.add(user);
        saveUsers(users);
    }

    // Retrieve users from SharedPreferences
    public List<User> getUsers() {
        String jsonStr = sharedPreferences.getString(USERS_KEY, "[]");
        List<User> users = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                users.add(User.fromJson(jsonObject));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Save list of users to SharedPreferences
    private void saveUsers(List<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            jsonArray.put(user.toJson());
        }
        sharedPreferences.edit().putString(USERS_KEY, jsonArray.toString()).apply();
    }
}
