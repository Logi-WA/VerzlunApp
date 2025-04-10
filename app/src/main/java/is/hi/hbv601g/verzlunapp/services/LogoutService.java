package is.hi.hbv601g.verzlunapp.services;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import is.hi.hbv601g.verzlunapp.network.ApiService;
import is.hi.hbv601g.verzlunapp.network.RetrofitClient;
import is.hi.hbv601g.verzlunapp.persistence.UserStorage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogoutService {

    private static final String TAG = "LogoutService";
    private final ApiService apiService;
    private final UserStorage userStorage;

    public LogoutService(Context context) {

        this.apiService = RetrofitClient.INSTANCE.getApiService();
        this.userStorage = new UserStorage(context);
    }

    /**
     * Performs logout actions: Clears local storage and attempts to call backend logout.
     * Note: This version is simplified. A robust implementation might use LiveData or a callback
     * to signal completion/failure back to the UI (e.g., AccountFragment).
     */
    public void performLogout() {
        Log.i(TAG, "Performing logout...");

        userStorage.clearLoggedInUser();
        Log.i(TAG, "Local user storage cleared.");

        apiService.logout(okhttp3.RequestBody.create(null, new byte[0])).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Backend logout endpoint notified successfully (Code: " + response.code() + ")");
                } else {
                    Log.w(TAG, "Backend logout endpoint notification failed (Code: " + response.code() + ")");

                    try {
                        if (response.errorBody() != null) {
                            Log.e(TAG, "Error body: " + response.errorBody().string());
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Network error calling backend logout endpoint", t);

            }
        });

    }
}