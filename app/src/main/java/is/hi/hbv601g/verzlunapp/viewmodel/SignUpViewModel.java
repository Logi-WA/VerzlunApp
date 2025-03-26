package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;

public class SignUpViewModel extends AndroidViewModel {
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>(false);

    private final UserStorage userStorage;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        userStorage = new UserStorage(application);
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

        // Save new user
        User newUser = new User(userName, userEmail, userPassword);
        userStorage.saveUser(newUser);

        // Reset error message on success
        errorMessage.setValue(null);
        signupSuccess.setValue(true);
    }
}
