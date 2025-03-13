package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import is.hi.hbv601g.verzlunapp.viewmodel.SignUpViewModel;
import is.hi.hbv601g.verzlunapp.databinding.FragmentSignupBinding;

public class SignUpFragment extends Fragment {
    private FragmentSignupBinding binding;
    private SignUpViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }
}
