package is.hi.hbv601g.verzlunapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SignUpViewModel extends ViewModel {
    public MutableLiveData<String> name = new MutableLiveData<>();
    public MutableLiveData<String> email = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    public void registerUser() {
        // Mock user registration logic
        System.out.println("Registering user: " + name.getValue() + ", " + email.getValue());
    }
}
