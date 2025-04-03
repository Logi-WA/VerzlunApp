package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

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
    private final UserService userService;
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();
    private static final String TAG = "SignUpViewModel";
    public MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>(false);
    public MutableLiveData<String> errorMessage = new MutableLiveData<>(null);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public SignUpViewModel(@NonNull Application application) {
        super(application);
        userService = new UserServiceImpl();
    }

    public void registerUser() {
        final String userName = name.getValue(); // Use final for lambda access
        final String userEmail = email.getValue();
        final String userPassword = password.getValue(); // Keep password locally for now

        // --- Input Validation ---
        if (userName == null || userName.trim().isEmpty()) {
            errorMessage.setValue("Name cannot be empty");
            return;
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            errorMessage.setValue("Email cannot be empty");
            return;
        }
        if (userPassword == null || userPassword.isEmpty()) {
            errorMessage.setValue("Password cannot be empty");
            return;
        }

        errorMessage.setValue(null); // Clear previous errors
        isLoading.setValue(true); // Show loading indicator

        // Create a SignupRequest object
        SignupRequest signupRequest = new SignupRequest(userName, userEmail, userPassword);

        // Use Retrofit to register the user
        Call<SignupResponse> call = RetrofitClient.INSTANCE.getApiService().signup(signupRequest);

        // Use enqueue for asynchronous execution
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(@NonNull Call<SignupResponse> call, @NonNull Response<SignupResponse> response) {
                isLoading.postValue(false); // Hide loading indicator (use postValue from background)

                if (response.isSuccessful() && response.body() != null) {
                    SignupResponse signupResponse = response.body();
                    // Check the 'success' flag from the backend response
                    if (signupResponse.getSuccess() && signupResponse.getData() != null) {
                        // Signup was successful according to the backend
                        String userId = signupResponse.getData().getId().toString(); // Get UUID as String
                        String responseName = signupResponse.getData().getName();
                        String responseEmail = signupResponse.getData().getEmail();

                        // Create User object with details from response (ID is crucial)
                        // Store empty string for password as it's not returned
                        User newUser = new User(userId, responseName, responseEmail, "");
                        userService.setCurrentUser(newUser); // Store the user with the correct ID

                        errorMessage.postValue(null); // Clear any previous error message
                        signupSuccess.postValue(true); // Signal success to the fragment
                        Log.i(TAG, "Signup successful for: " + responseEmail);
                    } else {
                        // Backend indicated failure
                        String errorMsg = signupResponse.getMessage() != null ? signupResponse.getMessage() : "Registration failed. Please try again.";
                        errorMessage.postValue(errorMsg);
                        Log.w(TAG, "Signup failed (API Success=false): " + errorMsg);
                    }
                } else {
                    // Handle unsuccessful HTTP responses
                    String error = "Registration failed. ";
                    if (response.code() == 409) { // Conflict - Email likely exists
                        error = "Email address already in use.";
                    } else {
                        // Try parsing error body if available
                        String errorBodyString = "";
                        if (response.errorBody() != null) {
                            try {
                                errorBodyString = response.errorBody().string();
                                // Try parsing the backend's ApiResponse structure from the error body
                                Gson gson = new Gson();
                                ApiResponseError apiError = gson.fromJson(errorBodyString, ApiResponseError.class);
                                if (apiError != null && apiError.getMessage() != null) {
                                    error = apiError.getMessage();
                                } else {
                                    error += "Server error (" + response.code() + ")";
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + errorBodyString, e);
                                error += "Server error (" + response.code() + ").";
                            }
                        } else {
                            error += "Server error (" + response.code() + ").";
                        }
                    }
                    errorMessage.postValue(error);
                    Log.e(TAG, "Signup HTTP error: " + response.code() + " - " + error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SignupResponse> call, @NonNull Throwable t) {
                isLoading.postValue(false); // Hide loading indicator
                errorMessage.postValue("Network error: " + t.getMessage());
                Log.e(TAG, "Signup network failure", t);
            }
        });
    }
}

class ApiResponseError {
    private String message;

    public String getMessage() {
        return message;
    }
}