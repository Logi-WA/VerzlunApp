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
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private FragmentEditProfileBinding binding;

    private UserStorage userStorage;
    private static final String TAG = "EditProfileFragment";
    private ApiService apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userStorage = new UserStorage(requireContext());
        apiService = RetrofitClient.INSTANCE.getApiService();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false);

        populateUserData();
        binding.saveChangesButton.setOnClickListener(v -> updateUserProfile());

        return binding.getRoot();
    }

    private void populateUserData() {
        User currentUser = userStorage.getLoggedInUser();
        if (currentUser != null) {
            binding.nameInput.setText(currentUser.getName());
            binding.emailInput.setText(currentUser.getEmail());
        } else {
            Log.e(TAG, "Current user data not found in UserStorage. Cannot edit profile.");
            Toast.makeText(requireContext(), "Error: User data not available. Please log in again.", Toast.LENGTH_LONG).show();

            if (getView() != null) {
                Navigation.findNavController(getView()).popBackStack();

            }
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

        User currentUserBeforeUpdate = userStorage.getLoggedInUser();
        if (currentUserBeforeUpdate != null &&
                name.equals(currentUserBeforeUpdate.getName()) &&
                email.equals(currentUserBeforeUpdate.getEmail())) {
            Toast.makeText(requireContext(), "No changes detected.", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.saveChangesButton.setEnabled(false);
        binding.saveChangesButton.setText("Saving...");

        Map<String, String> userDetails = new HashMap<>();
        userDetails.put("name", name);
        userDetails.put("email", email);

        Call<GenericApiResponse<UserData>> call = apiService.updateProfile(userDetails);

        call.enqueue(new Callback<GenericApiResponse<UserData>>() {
            @Override
            public void onResponse(@NonNull Call<GenericApiResponse<UserData>> call, @NonNull Response<GenericApiResponse<UserData>> response) {

                if (getActivity() == null || binding == null) {
                    Log.w(TAG, "Fragment detached during API response.");
                    return;
                }

                getActivity().runOnUiThread(() -> {
                    binding.saveChangesButton.setEnabled(true);
                    binding.saveChangesButton.setText("Save Changes");

                    if (response.isSuccessful() && response.body() != null && response.body().getSuccess()) {
                        UserData updatedApiData = response.body().getData();
                        User currentUser = userStorage.getLoggedInUser();

                        if (currentUser != null && updatedApiData != null) {
                            User updatedUserForStorage = new User(
                                    currentUser.getUserId(),
                                    updatedApiData.getName(),
                                    updatedApiData.getEmail(),
                                    ""
                            );
                            updatedUserForStorage.setAuthToken(currentUser.getAuthToken());

                            userStorage.saveLoggedInUser(updatedUserForStorage);
                            Log.i(TAG, "User profile updated in UserStorage.");

                            Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();

                            if (getView() != null) {
                                Navigation.findNavController(getView()).popBackStack();
                            }

                        } else {

                            Log.e(TAG, "Failed to update UserStorage. CurrentUser: " + (currentUser != null) + ", UpdatedApiData: " + (updatedApiData != null));
                            Toast.makeText(requireContext(), "Update successful but failed to save locally.", Toast.LENGTH_LONG).show();
                            if (getView() != null) {
                                Navigation.findNavController(getView()).popBackStack();
                            }
                        }

                    } else {

                        String error = "Update failed. ";
                        if (response.code() == 409) {
                            error = "Email address already in use.";
                        } else if (response.body() != null && response.body().getMessage() != null) {
                            error = response.body().getMessage();
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

                if (getActivity() == null || binding == null) {
                    Log.w(TAG, "Fragment detached during API failure.");
                    return;
                }

                getActivity().runOnUiThread(() -> {
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