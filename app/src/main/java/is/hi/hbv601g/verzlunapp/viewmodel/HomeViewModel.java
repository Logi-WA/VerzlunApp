package is.hi.hbv601g.verzlunapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class HomeViewModel extends AndroidViewModel {
    public MutableLiveData<String> pageTitle = new MutableLiveData<>("Home");

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }
}
