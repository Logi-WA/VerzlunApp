package is.hi.hbv601g.verzlunapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import is.hi.hbv601g.verzlunapp.fragments.SignInFragment;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;

import java.io.IOException;
import java.io.InputStream;


public class VerzlunActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the NavHostFragment safely
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
        } else {
            Log.e("VerzlunActivity", "NavHostFragment is null!");
        }
    }


    private void initializeSampleUsers() {
        UserStorage storage = new UserStorage(this);
        if (!storage.getUsers().isEmpty()) {
            return;
        }

        try (InputStream is = getResources().openRawResource(R.raw.sample_users)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJson = jsonArray.getJSONObject(i);
                User user = User.fromJson(userJson);
                if (user != null) {
                    storage.saveUser(user);
                }
            }
        } catch (Resources.NotFoundException e) {
            Log.e("VerzlunActivity", "Sample users resource not found", e);
        } catch (IOException e) {
            Log.e("VerzlunActivity", "Error reading sample users file", e);
        } catch (JSONException e) {
            Log.e("VerzlunActivity", "Error parsing sample users JSON", e);
        }
    }
}


