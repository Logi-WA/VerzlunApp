package is.hi.hbv601g.verzlunapp.Services.ServiceImplementations;

import is.hi.hbv601g.verzlunapp.Persistence.User;
import is.hi.hbv601g.verzlunapp.Services.UserService;

public class UserServiceImpl implements UserService {
    private static User currentUser;

    @Override
    public boolean registerUser(User user) {
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
}
