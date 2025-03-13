package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;

import java.util.List;

public class SignInViewModel extends AndroidViewModel {
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAuthenticated = new MutableLiveData<>(false);

    private UserStorage userStorage;

    public SignInViewModel(@NonNull Application application) {
        super(application);
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

        // Authenticate against stored users
        List<User> users = userStorage.getUsers();
        for (User user : users) {
            if (user.getEmail().equals(userEmail) && user.getPassword().equals(userPassword)) {
                isAuthenticated.setValue(true);
                errorMessage.setValue(null);
                return;
            }
        }

        errorMessage.setValue("Invalid email or password");
    }
}
