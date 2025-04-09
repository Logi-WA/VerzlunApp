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
import androidx.navigation.Navigation;
import androidx.room.Room;

import is.hi.hbv601g.verzlunapp.R;
import is.hi.hbv601g.verzlunapp.database.UserInfo;
import is.hi.hbv601g.verzlunapp.database.UserInfoDatabase;
import is.hi.hbv601g.verzlunapp.database.UserInfoDatabaseHandler;
import is.hi.hbv601g.verzlunapp.databinding.FragmentSigninBinding;
import is.hi.hbv601g.verzlunapp.viewmodel.SignInViewModel;

public class SignInFragment extends Fragment {
    private FragmentSigninBinding binding;
    private SignInViewModel viewModel;

    private UserInfoDatabaseHandler dbh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSigninBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        observeViewModel();
        setupClickListeners();

        UserInfoDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(), UserInfoDatabase.class, "user_db").build();
        dbh = new UserInfoDatabaseHandler(db);
        UserInfo ui = dbh.getUserInfo();

        if (ui != null) {
            System.out.println("Not null");
            viewModel.fillIn(ui.getEmail(), ui.getPassword());
        }

        return binding.getRoot();
    }

    private void observeViewModel() {
        viewModel.errorMessage.observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.isAuthenticated.observe(getViewLifecycleOwner(), authenticated -> {
            if (authenticated != null && authenticated) {
                UserInfo ui =  new UserInfo(
                        1,
                        binding.signinInputEmail.getText().toString(),
                        binding.signinInputPassword.getText().toString());

                dbh.insertUserInfo(ui);
                UserInfo ui2 = dbh.getUserInfo();

                if (ui2 != null) {
                    System.out.println("Email: " + ui.getEmail() + "\tPassword: " + ui.getPassword());
                }
                else {
                    System.out.println("Null");
                }

                Navigation.findNavController(binding.getRoot())
                        .navigate(R.id.homeFragment); // Use correct ID from your nav_graph
            }
        });
    }

    private void setupClickListeners() {
        binding.signinToSignupLink.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.signUpFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
