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

import is.hi.hbv601g.verzlunapp.databinding.FragmentAccountBinding;
import is.hi.hbv601g.verzlunapp.services.LogoutService;
import is.hi.hbv601g.verzlunapp.services.NetworkService;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;
    private LogoutService logoutService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        NetworkService networkService = NetworkServiceImpl.getInstance();
        UserService userService = new UserServiceImpl();
        logoutService = new LogoutService(networkService, userService);

        setupUserData(userService);

        binding.editProfileButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(is.hi.hbv601g.verzlunapp.R.id.editProfileFragment));

        binding.changePasswordButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(is.hi.hbv601g.verzlunapp.R.id.changePasswordFragment));

        binding.logoutButton.setOnClickListener(v -> {
            new Thread(() -> {
                boolean success = logoutService.logoutUser();
                getActivity().runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(v).navigate(is.hi.hbv601g.verzlunapp.R.id.signInFragment);
                    } else {
                        Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }).start();
        });

        return binding.getRoot();
    }

    private void setupUserData(UserService userService) {
        if (userService.getCurrentUser() != null) {
            binding.accountName.setText(userService.getCurrentUser().getName());
            binding.accountEmail.setText(userService.getCurrentUser().getEmail());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
