package is.hi.hbv601g.verzlunapp;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;

public class VerzlunActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();

            // Navbar visibility toggle logic
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                View navbar = findViewById(R.id.globalNavbar);
                if (navbar != null) {
                    if (destination.getId() == R.id.signInFragment || destination.getId() == R.id.signUpFragment) {
                        navbar.setVisibility(View.GONE);
                    } else {
                        navbar.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        setupNavbarListeners();
        initializeSampleUsers();
    }

    private void setupNavbarListeners() {
        ImageView menu = findViewById(R.id.navMenuButton);
        ImageView cart = findViewById(R.id.navCartButton);
        ImageView wishlist = findViewById(R.id.navWishlistButton);
        ImageView account = findViewById(R.id.navAccountButton);

        if (menu != null) {
            menu.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(this, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_categories, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("categoryName", item.getTitle().toString());
                    navController.navigate(R.id.categoriesFragment, bundle);
                    return true;
                });
                popupMenu.show();
            });
        }

        if (cart != null) {
            cart.setOnClickListener(v -> navController.navigate(R.id.cartFragment));
        }

        if (wishlist != null) {
            wishlist.setOnClickListener(v -> navController.navigate(R.id.wishlistFragment));
        }

        if (account != null) {
            account.setOnClickListener(v -> navController.navigate(R.id.accountFragment));
        }
    }

    private void initializeSampleUsers() {
        UserStorage storage = new UserStorage(this);
        if (!storage.getUsers().isEmpty()) return;

        try (InputStream is = getResources().openRawResource(R.raw.sample_users)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String json = new String(buffer, "UTF-8");

            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJson = jsonArray.getJSONObject(i);
                User user = User.fromJson(userJson);
                if (user != null) storage.saveUser(user);
            }
        } catch (Resources.NotFoundException | IOException | JSONException e) {
            Log.e("VerzlunActivity", "Failed to load sample users", e);
        }
    }
}