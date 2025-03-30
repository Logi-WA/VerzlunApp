package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import is.hi.hbv601g.verzlunapp.model.SignupRequest;
import is.hi.hbv601g.verzlunapp.model.SignupResponse;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.UserService;
import is.hi.hbv601g.verzlunapp.services.serviceimplementations.UserServiceImpl;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends AndroidViewModel {
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    public MutableLiveData<String> errorMessage = new MutableLiveData<>();
    public MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>(false);

    private final UserService userService;

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        userService = new UserServiceImpl();
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

        // Create a SignupRequest object
        SignupRequest signupRequest = new SignupRequest(userName, userEmail, userPassword);

        // Use Retrofit to register the user
        Call<SignupResponse> call = RetrofitClient.INSTANCE.getApiService().signup(signupRequest);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User newUser = new User(userName, userEmail, userPassword);
                    userService.setCurrentUser(newUser);
                    errorMessage.setValue(null);
                    signupSuccess.setValue(true);
                } else {
                    String error = "Registration failed. ";
                    if (response.code() == 409) {
                        error += "Email already in use.";
                    } else {
                        error += "Please try again.";
                    }
                    errorMessage.setValue(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignupResponse> call, @NonNull Throwable t) {
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}