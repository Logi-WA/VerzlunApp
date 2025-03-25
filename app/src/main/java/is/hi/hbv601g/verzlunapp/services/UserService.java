package is.hi.hbv601g.verzlunapp.services;

import is.hi.hbv601g.verzlunapp.persistence.User;

public interface UserService {
    boolean registerUser(User user);
    User getCurrentUser();
}
