package is.hi.hbv601g.verzlunapp.Services;

import is.hi.hbv601g.verzlunapp.Persistence.User;

public interface UserService {
    boolean registerUser(User user);
    User getCurrentUser();
}
