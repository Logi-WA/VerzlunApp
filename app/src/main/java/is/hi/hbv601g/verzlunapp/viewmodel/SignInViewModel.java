package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import is.hi.hbv601g.verzlunapp.services.SignInService;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;

public class SignInViewModel extends AndroidViewModel {
    private final SignInService signInService;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SignInViewModel(@NonNull Application application) {
        super(application);
        UserService userService = new UserServiceImpl();
        signInService = new SignInService(userService);
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

        isLoading.setValue(true);

        executor.execute(() -> {
            boolean success = signInService.loginUser(userEmail, userPassword);

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
