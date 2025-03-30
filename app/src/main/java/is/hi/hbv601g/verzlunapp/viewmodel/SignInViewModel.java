package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import is.hi.hbv601g.verzlunapp.services.NetworkService;
import is.hi.hbv601g.verzlunapp.services.SignInService;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.NetworkServiceImpl;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;

public class SignInViewModel extends AndroidViewModel {
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);
    private final SignInService signInService;
    private final Executor executor = Executors.newSingleThreadExecutor();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SignInViewModel(@NonNull Application application) {
        super(application);
        NetworkService networkService = NetworkServiceImpl.getInstance();
        UserService userService = new UserServiceImpl();
        signInService = new SignInService(networkService, userService);
    }

    public void loginUser() {
        String userEmail = email.getValue();
        String userPassword = password.getValue();

        if (userEmail == null || userEmail.isEmpty()) {
            errorMessage.setValue("Email cannot be empty");
            return;
        }

        if (userPassword == null || userPassword.isEmpty()) {
            errorMessage.setValue("Password cannot be empty");
            return;
        }

        // Show loading state
        isLoading.setValue(true);

        // Authenticate via API on background thread
        executor.execute(() -> {
            boolean success = signInService.loginUser(userEmail, userPassword);

            // Update UI on main thread
            isLoading.postValue(false);

            if (success) {
                isAuthenticated.postValue(true);
                errorMessage.postValue(null);
            } else {
                isAuthenticated.postValue(false);
                errorMessage.postValue("Invalid email or password");
            }
        });
    }
}