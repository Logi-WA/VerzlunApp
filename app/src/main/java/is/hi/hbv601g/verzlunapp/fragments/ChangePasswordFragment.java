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
import androidx.room.Room;

import java.util.HashMap;
import java.util.Map;

import is.hi.hbv601g.verzlunapp.database.UserInfo;
import is.hi.hbv601g.verzlunapp.database.UserInfoDatabase;
import is.hi.hbv601g.verzlunapp.database.UserInfoDatabaseHandler;
import is.hi.hbv601g.verzlunapp.databinding.FragmentChangePasswordBinding;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordFragment extends Fragment {
    private FragmentChangePasswordBinding binding;
    private UserInfoDatabaseHandler dbh;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false);
        binding.changePasswordButton.setOnClickListener(v -> changePassword());
        UserInfoDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), UserInfoDatabase.class, "user_db").build();
        dbh = new UserInfoDatabaseHandler(db);
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

        // Create request payload
        Map<String, String> passwordDetails = new HashMap<>();
        passwordDetails.put("currentPassword", currentPassword);
        passwordDetails.put("newPassword", newPassword);

        // Execute the API call with Retrofit
        Call<ResponseBody> call = RetrofitClient.INSTANCE.getApiService().changePassword(passwordDetails);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (getActivity() == null) return;

                binding.changePasswordButton.setEnabled(true);
                binding.changePasswordButton.setText("Change Password");

                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                    UserInfo ui = dbh.getUserInfo();
                    ui = new UserInfo(1, ui.getEmail(), newPassword);
                    dbh.insertUserInfo(ui);
                    Navigation.findNavController(requireView()).popBackStack();
                } else {
                    if (response.code() == 400) {
                        Toast.makeText(requireContext(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (getActivity() == null) return;

                binding.changePasswordButton.setEnabled(true);
                binding.changePasswordButton.setText("Change Password");
                Toast.makeText(requireContext(), "Connection error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
