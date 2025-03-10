package is.hi.hbv601g.verzlunapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONArray;
import org.json.JSONObject;

import is.hi.hbv601g.verzlunapp.Fragments.SignInFragment;
import is.hi.hbv601g.verzlunapp.Persistence.User;
import is.hi.hbv601g.verzlunapp.Persistence.UserStorage;

import java.io.InputStream;


public class VerzlunActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSampleUsers();
        loadFragment(new SignInFragment());
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void initializeSampleUsers() {
        try {
            InputStream is = getResources().openRawResource(R.raw.sample_users);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);
            UserStorage storage = new UserStorage(this);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJson = jsonArray.getJSONObject(i);
                User user = User.fromJson(userJson);
                if (user != null) {
                    storage.saveUser(user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


