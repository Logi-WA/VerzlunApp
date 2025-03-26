package is.hi.hbv601g.verzlunapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import is.hi.hbv601g.verzlunapp.databinding.FragmentAccountBinding;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);

        binding.editProfileButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(is.hi.hbv601g.verzlunapp.R.id.editProfileFragment));

        binding.changePasswordButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(is.hi.hbv601g.verzlunapp.R.id.changePasswordFragment));

        binding.logoutButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(is.hi.hbv601g.verzlunapp.R.id.signInFragment));

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
