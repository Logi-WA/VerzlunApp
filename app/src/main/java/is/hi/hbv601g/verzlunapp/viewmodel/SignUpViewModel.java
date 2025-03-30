package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.NetworkService;
import is.hi.hbv601g.verzlunapp.services.SignUpService;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;

public class SignUpViewModel extends AndroidViewModel {
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>(false);

    private final SignUpService signUpService;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        NetworkService networkService = NetworkServiceImpl.getInstance();
        UserService userService = new UserServiceImpl();
        signUpService = new SignUpService(networkService, userService);
    }

    public void registerUser() {
        String userName = name.getValue();
        String userEmail = email.getValue();
        String userPassword = password.getValue();

        if (userName == null || userName.isEmpty()) {
            errorMessage.setValue("Name cannot be empty");
            return;
        }

        if (userEmail == null || userEmail.isEmpty()) {
            errorMessage.setValue("Email cannot be empty");
            return;
        }

        if (userPassword == null || userPassword.isEmpty()) {
            errorMessage.setValue("Password cannot be empty");
            return;
        }

        // Create and register user through the API
        User newUser = new User(userName, userEmail, userPassword);
        boolean success = signUpService.registerUser(newUser);

        if (success) {
            errorMessage.setValue(null);
            signupSuccess.setValue(true);
        } else {
            errorMessage.setValue("Registration failed. Please try again.");
        }
    }
}
