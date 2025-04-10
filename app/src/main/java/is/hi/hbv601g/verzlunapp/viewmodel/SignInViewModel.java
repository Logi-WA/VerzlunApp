package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;
import is.hi.hbv601g.verzlunapp.services.SignInService;

public class SignInViewModel extends AndroidViewModel {
    private final SignInService signInService;
    private static final String TAG = "SignInViewModel";
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final UserStorage userStorage;

    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SignInViewModel(@NonNull Application application) {
        super(application);

        signInService = new SignInService();
        userStorage = new UserStorage(application);
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
        errorMessage.setValue(null);

        executor.execute(() -> {

            User loggedInUser = signInService.loginUser(userEmail, userPassword);

            isLoading.postValue(false);

            if (loggedInUser != null && loggedInUser.getAuthToken() != null) {
                Log.i(TAG, "Login successful, saving user and token.");

                userStorage.saveLoggedInUser(loggedInUser);

                isAuthenticated.postValue(true);
                errorMessage.postValue(null);
            } else {
                Log.w(TAG, "Login failed or user/token was null.");
                isAuthenticated.postValue(false);

                errorMessage.postValue("Invalid email or password");

                userStorage.clearLoggedInUser();
            }
        });
    }
}