package is.hi.hbv601g.verzlunapp.persistence;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
    private static final String AUTH_TOKEN_KEY = "auth_token";

    private SharedPreferences sharedPreferences;

    public UserStorage(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public List<User> getUsers() {
        String jsonStr = sharedPreferences.getString(USERS_KEY, "[]");
        List<User> users = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                User u = User.fromJson(jsonObject);
                if (u != null) users.add(u);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }

    private void saveUsersList(List<User> users) {
        JSONArray jsonArray = new JSONArray();
        for (User user : users) {
            jsonArray.put(user.toJson());
        }
        sharedPreferences.edit().putString(USERS_KEY, jsonArray.toString()).apply();
    }

    /**
     * Saves the essential details of the currently logged-in user.
     * Call this after a successful login.
     *
     * @param user The user who just logged in.
     */
    public void saveLoggedInUser(User user) {
        if (user == null) {
            clearLoggedInUser();
            return;
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LOGGED_IN_USER_ID_KEY, user.getUserId());
        editor.putString(LOGGED_IN_USER_NAME_KEY, user.getName());
        editor.putString(LOGGED_IN_USER_EMAIL_KEY, user.getEmail());
        editor.putString(AUTH_TOKEN_KEY, user.getAuthToken());
        editor.apply();
    }

    /**
     * Retrieves the details of the currently logged-in user.
     * Returns null if no user is logged in.
     *
     * @return The logged-in User object or null.
     */
    public User getLoggedInUser() {
        String userId = sharedPreferences.getString(LOGGED_IN_USER_ID_KEY, null);
        String name = sharedPreferences.getString(LOGGED_IN_USER_NAME_KEY, null);
        String email = sharedPreferences.getString(LOGGED_IN_USER_EMAIL_KEY, null);
        String token = sharedPreferences.getString(AUTH_TOKEN_KEY, null);

        if (!TextUtils.isEmpty(email)) {
            User loggedInUser = new User(userId, name, email, "");
            loggedInUser.setAuthToken(token);
            return loggedInUser;
        } else {
            return null;
        }
    }

    /**
     * Clears the saved details of the logged-in user.
     * Call this on logout.
     */
    public void clearLoggedInUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(LOGGED_IN_USER_ID_KEY);
        editor.remove(LOGGED_IN_USER_NAME_KEY);
        editor.remove(LOGGED_IN_USER_EMAIL_KEY);
        editor.remove(AUTH_TOKEN_KEY);
        editor.apply();
    }

    /**
     * Checks if a user is currently logged in (based on stored email).
     *
     * @return true if a user's email is stored, false otherwise.
     */
    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(sharedPreferences.getString(LOGGED_IN_USER_EMAIL_KEY, null));
    }

    /**
     * Saves ONLY the auth token. Useful if token is refreshed separately.
     *
     * @param token The new auth token.
     */
    public void saveAuthToken(String token) {
        sharedPreferences.edit().putString(AUTH_TOKEN_KEY, token).apply();
    }

    /**
     * Retrieves ONLY the auth token.
     *
     * @return The stored auth token, or null if not found.
     */
    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN_KEY, null);
    }
}