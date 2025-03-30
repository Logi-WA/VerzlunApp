package is.hi.hbv601g.verzlunapp.services.serviceimplementations;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.UserService;

public class UserServiceImpl implements UserService {
    private static User currentUser;

    @Override
    public boolean registerUser(User user) {
        // This method will now be handled by the SignUpService
        if (user.getEmail().contains("@") && user.getPassword().length() >= 6) {
            currentUser = user;
            return true;
        }
        return false;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        currentUser = user;
    }
}
