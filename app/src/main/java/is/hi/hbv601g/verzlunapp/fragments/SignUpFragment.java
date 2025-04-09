package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.room.Room;

import org.json.JSONException;
import org.json.JSONObject;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.database.UserInfo;
import is.hi.hbv601g.verzlunapp.database.UserInfoDatabase;
import is.hi.hbv601g.verzlunapp.database.UserInfoDatabaseHandler;
import is.hi.hbv601g.verzlunapp.databinding.FragmentSignupBinding;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;

public class SignUpFragment extends Fragment {
    private FragmentSignupBinding binding;
    private static final String TAG = "SignUpFragment";
    private UserInfoDatabaseHandler dbh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        setupClickListeners();
        UserInfoDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), UserInfoDatabase.class, "user_db").build();
        dbh = new UserInfoDatabaseHandler(db);
        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.signupToSigninLink.setOnClickListener(v ->
                Navigation.findNavController(binding.getRoot()).navigate(R.id.signInFragment)
        );

        System.out.println("setting up listeners");
        binding.signupButton.setOnClickListener(v -> {
            System.out.println("Signing up!");
            String name = binding.signupInputName.getText().toString().trim();
            String email = binding.signupInputEmail.getText().toString().trim();
            String password = binding.signupInputPassword.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields.");
                return;
            }

            JSONObject payload = new JSONObject();
            try {
                payload.put("name", name);
                payload.put("email", email);
                payload.put("password", password);
            } catch (JSONException e) {
                Log.e(TAG, "Error building signup payload", e);
                showToast("Something went wrong.");
                return;
            }

            new Thread(() -> {
                String response = NetworkServiceImpl.getInstance().post("/auth/register", payload.toString());

                if (response != null) {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONObject data = json.getJSONObject("data");
                        String token = data.getString("token");

                        NetworkServiceImpl.getInstance().setAuthToken(token);

                        requireActivity().runOnUiThread(() -> {
                            showToast("Signup successful!");
                            UserInfo ui = new UserInfo(1, email, password);
                            dbh.insertUserInfo(ui);

                            Navigation.findNavController(binding.getRoot()).navigate(R.id.homeFragment);
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing signup response", e);
                        showToast("Signup succeeded but response was invalid.");
                    }
                } else {
                    Log.e(TAG, "Signup failed: response was null");
                    showToast("Signup failed. Please try again.");
                }
            }).start();
        });
    }

    private void showToast(String msg) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
