package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import java.util.HashMap;
import java.util.Map;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.databinding.FragmentEditProfileBinding;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.NetworkService;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private UserService userService;
    private NetworkService networkService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        networkService = NetworkServiceImpl.getInstance();
        userService = new UserServiceImpl();
        populateUserData();
        binding.saveChangesButton.setOnClickListener(v -> updateUserProfile());
        return binding.getRoot();
    }

    private void populateUserData() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            binding.nameInput.setText(currentUser.getName());
            binding.emailInput.setText(currentUser.getEmail());
        } else {
            fetchUserProfile();
        }
    }

    private void fetchUserProfile() {
        new Thread(() -> {

            if (!((NetworkServiceImpl) networkService).isAuthenticated()) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Session expired, please log in again", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).navigate(R.id.signInFragment);
                });
                return;
            }

            String response = networkService.get("/api/users/me");
            if (response != null) {
                try {
                    org.json.JSONObject userJson = new org.json.JSONObject(response);
                    String name = userJson.getString("name");
                    String email = userJson.getString("email");

                    getActivity().runOnUiThread(() -> {
                        binding.nameInput.setText(name);
                        binding.emailInput.setText(email);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateUserProfile() {
        String name = binding.nameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameInput.setError("Name cannot be empty");
            return;
        }

        if (!email.contains("@")) {
            binding.emailInput.setError("Enter a valid email address");
            return;
        }

        // Loading state
        binding.saveChangesButton.setEnabled(false);
        binding.saveChangesButton.setText("Saving...");

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("email", email);

        // Execute API call with Retrofit
        Call<Object> call = RetrofitClient.INSTANCE.getApiService().updateProfile(userDetails);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Object> call, @NonNull Response<Object> response) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    binding.saveChangesButton.setEnabled(true);
                    binding.saveChangesButton.setText("Save Changes");

                    if (response.isSuccessful()) {
                        // Update current user in memory
                        User user = userService.getCurrentUser();
                        if (user != null) {
                            user.setName(name);
                            user.setEmail(email);
                            userService.setCurrentUser(user);
                        }

                        Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<Object> call, @NonNull Throwable t) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    binding.saveChangesButton.setEnabled(true);
                    binding.saveChangesButton.setText("Save Changes");
                    Toast.makeText(requireContext(), "Connection error", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
