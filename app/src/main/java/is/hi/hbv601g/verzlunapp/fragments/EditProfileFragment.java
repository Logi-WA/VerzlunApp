package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import is.hi.hbv601g.verzlunapp.databinding.FragmentEditProfileBinding;
import is.hi.hbv601g.verzlunapp.model.ApiResponseError;
import is.hi.hbv601g.verzlunapp.model.GenericApiResponse;
import is.hi.hbv601g.verzlunapp.model.UserData;
import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;
    private UserService userService;
    //    private NetworkService networkService;
    private static final String TAG = "EditProfileFragment";
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);
        // networkService = NetworkServiceImpl.getInstance(); // Remove
        userService = new UserServiceImpl();
        apiService = RetrofitClient.INSTANCE.getApiService(); // Get ApiService instance

        populateUserData(); // Populate fields initially
        binding.saveChangesButton.setOnClickListener(v -> updateUserProfile());
        return binding.getRoot();
    }

    private void populateUserData() {
        User currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            binding.nameInput.setText(currentUser.getName());
            binding.emailInput.setText(currentUser.getEmail());
        } else {
            // Handle case where user data isn't available (e.g., fetch again or navigate back)
            Log.w(TAG, "Current user data not found in UserService. Navigating back.");
            Toast.makeText(requireContext(), "User data not available.", Toast.LENGTH_SHORT).show();
            // Optional: Navigate back if user data is essential here
            // Navigation.findNavController(requireView()).popBackStack();
        }
    }

    private void updateUserProfile() {
        String name = binding.nameInput.getText().toString().trim();
        String email = binding.emailInput.getText().toString().trim();

        if (name.isEmpty()) {
            binding.nameInput.setError("Name cannot be empty");
            return;
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.setError("Enter a valid email address");
            return;
        }

        // Loading state
        binding.saveChangesButton.setEnabled(false);
        binding.saveChangesButton.setText("Saving...");

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("email", email);

        Call<GenericApiResponse<UserData>> call = apiService.updateProfile(userDetails);

        call.enqueue(new Callback<GenericApiResponse<UserData>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<UserData>> call, @NonNull Response<GenericApiResponse<UserData>> response) {
                if (getActivity() == null || binding == null) return; // Check fragment state

                getActivity().runOnUiThread(() -> { // Ensure UI updates on main thread
                    binding.saveChangesButton.setEnabled(true);
                    binding.saveChangesButton.setText("Save Changes");

                    if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                        // Profile updated successfully on backend
                        UserData updatedUserData = response.body().getData();

                        if (updatedUserData != null) {
                            // Update current user in memory with data from response
                            User currentUser = userService.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.setName(updatedUserData.getName());
                                currentUser.setEmail(updatedUserData.getEmail());
                                // ID doesn't change, keep existing ID
                                userService.setCurrentUser(currentUser);
                                Log.i(TAG, "User profile updated locally.");
                            }
                        } else {
                            Log.w(TAG, "Profile updated on backend, but no user data returned in response.");
                        }

                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(binding.getRoot()).popBackStack();
                    } else {
                        // Handle unsuccessful HTTP responses
                        String error = "Update failed. ";
                        if (response.code() == 409) { // Conflict - Email likely exists
                            error = "Email address already in use.";
                        } else if (response.body() != null && response.body().getMessage() != null) {
                            error = response.body().getMessage(); // Use backend message
                        } else if (response.errorBody() != null) {
                            try {
                                String errorBodyString = response.errorBody().string();
                                Gson gson = new Gson();
                                ApiResponseError apiError = gson.fromJson(errorBodyString, ApiResponseError.class);
                                if (apiError != null && apiError.getMessage() != null) {
                                    error = apiError.getMessage();
                                } else {
                                    error += "Server error (" + response.code() + ").";
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body", e);
                                error += "Server error (" + response.code() + ").";
                            }
                        } else {
                            error += "Server error (" + response.code() + ").";
                        }
                        Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Update profile failed: " + error);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<GenericApiResponse<UserData>> call, @NonNull Throwable t) {
                if (getActivity() == null || binding == null) return; // Check fragment state

                getActivity().runOnUiThread(() -> { // Ensure UI updates on main thread
                    binding.saveChangesButton.setEnabled(true);
                    binding.saveChangesButton.setText("Save Changes");
                    Toast.makeText(requireContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Update profile network failure", t);
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
