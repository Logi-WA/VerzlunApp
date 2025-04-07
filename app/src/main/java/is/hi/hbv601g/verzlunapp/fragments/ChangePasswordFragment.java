package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.util.Log;
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

import is.hi.hbv601g.verzlunapp.databinding.FragmentChangePasswordBinding;
import is.hi.hbv601g.verzlunapp.model.ApiResponseError;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    private FragmentChangePasswordBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        binding.changePasswordButton.setOnClickListener(v -> changePassword());
        return binding.getRoot();
    }

    private void changePassword() {
        String currentPassword = binding.currentPassword.getText().toString();
        String newPassword = binding.newPassword.getText().toString();

        if (currentPassword.isEmpty()) {
            binding.currentPassword.setError("Current password is required");
            return;
        }

        if (newPassword.isEmpty()) {
            binding.newPassword.setError("New password is required");
            return;
        }

        if (newPassword.length() < 4) {
            binding.newPassword.setError("Password must be at least 4 characters");
            return;
        }

        // Loading state
        binding.changePasswordButton.setEnabled(false);
        binding.changePasswordButton.setText("Changing Password...");

        Map<String, String> passwordDetails = new HashMap<>();
        passwordDetails.put("currentPassword", currentPassword);
        passwordDetails.put("newPassword", newPassword);

        Call<ResponseBody> call = RetrofitClient.INSTANCE.getApiService().changePassword(passwordDetails);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (getActivity() == null || binding == null) return; // Check fragment state

                getActivity().runOnUiThread(() -> { // Ensure UI updates on main thread
                    binding.changePasswordButton.setEnabled(true);
                    binding.changePasswordButton.setText("Change Password");

                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).popBackStack();
                    } else {
                        // Error handling
                        String errorMsg = "Failed to change password";
                        if (response.errorBody() != null) {
                            try {
                                String errorBodyString = response.errorBody().string();
                                Gson gson = new Gson();
                                ApiResponseError apiError = gson.fromJson(errorBodyString, ApiResponseError.class);
                                if (apiError != null && apiError.getMessage() != null) {
                                    errorMsg = apiError.getMessage(); // Use backend message
                                } else {
                                    errorMsg += ". Server error (" + response.code() + ").";
                                }
                            } catch (Exception e) {
                                Log.e("ChangePasswordFragment", "Error parsing error body", e);
                                errorMsg += ". Server error (" + response.code() + ").";
                            }
                        } else {
                            errorMsg += ". Server error (" + response.code() + ").";
                        }
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                        Log.e("ChangePasswordFragment", "Change password failed: " + errorMsg);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (getActivity() == null || binding == null) return; // Check fragment state

                getActivity().runOnUiThread(() -> { // Ensure UI updates on main thread
                    binding.changePasswordButton.setEnabled(true);
                    binding.changePasswordButton.setText("Change Password");
                    Toast.makeText(requireContext(), "Connection error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ChangePasswordFragment", "Change password network failure", t);
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
