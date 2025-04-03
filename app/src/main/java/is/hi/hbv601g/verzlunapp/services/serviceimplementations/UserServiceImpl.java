package is.hi.hbv601g.verzlunapp.services.serviceimplementations;

import is.hi.hbv601g.verzlunapp.persistence.User;
import is.hi.hbv601g.verzlunapp.services.UserService;

public class UserServiceImpl implements UserService {
    private static User currentUser;

    @Override
    public User getCurrentUser() {
        return currentUser;
    }

    @Override
    public void setCurrentUser(User user) {
        currentUser = user;
    }
}