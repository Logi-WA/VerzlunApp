package is.hi.hbv601g.verzlunapp;

import android.app.Application;

import is.hi.hbv601g.verzlunapp.network.RetrofitClient;

public class VerzlaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        RetrofitClient.INSTANCE.initialize(this);
    }
}