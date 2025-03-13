package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.viewmodel.SignInViewModel;
import is.hi.hbv601g.verzlunapp.databinding.FragmentSigninBinding;

public class SignInFragment extends Fragment {
    private FragmentSigninBinding binding;
    private SignInViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        setupObservers();
        setupClickListeners();

        return binding.getRoot();
    }

    private void setupObservers() {
        viewModel.isAuthenticated.observe(getViewLifecycleOwner(), isAuthenticated -> {
            if (isAuthenticated) {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();
                // Navigate to the main content
                // Could use Navigation component or FragmentTransaction
            }
        });
    }

    private void setupClickListeners() {
        binding.signinToSignupLink.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SignUpFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
