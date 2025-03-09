package is.hi.hbv601g.verzlunapp.Services;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import javax.sql.DataSource;

import is.hi.hbv601g.verzlunapp.Persistence.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Network {
    // Credentials
    private String BASE_URL = "jdbc:postgresql://cav8p52l9arddb.cluster-czz5s0kz4scl.eu-west-1.rds.amazonaws.com:5432/d3600pijll20dj?sslmode=require";
    private String USERNAME = "uecsnb8nr2hd0e";
    private String PASSWORD = "pf4580202c8a6cca374c37e81a99b531788b15e937d204401cff7c626626ab8c5";
    private String DRIVER_CLASS_NAME = "org.postgresql.Driver";

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private final OkHttpClient mClient = new OkHttpClient();

    //public JSONObject getRequest(String api) {

    //}

    public void registerUser(String username, String email, String password) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(formBody)
                .build();

        Call call = mClient.newCall(request);
        Response response = call.execute();


    }
    public void getRequest() throws IOException {
        Request request = new Request.Builder().url( + "/api/").build();

        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });
        Response response = call.execute();
    }

    public void postRequest() {

    }

    public void updateRequest() {

    }
}
