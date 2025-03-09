package is.hi.hbv601g.verzlunapp.Services;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Network {
    private final String BASE_URL = "localhost:8080/api";
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient mClient;

    public Network() {
        this.mClient = new OkHttpClient();
    }

    public void getTest() {
        get();
    }
    public String get() {
        System.out.println("Get Request 1");
        Request request = new Request.Builder().url(BASE_URL+"/products").build();
        System.out.println("Get Request 2");

        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("Failure");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                System.out.println("Got a response");
            }
        });
        System.out.println("Get Request end");
        return "";
    }
}
