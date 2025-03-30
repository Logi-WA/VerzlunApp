package is.hi.hbv601g.verzlunapp.services;

import is.hi.hbv601g.verzlunapp.persistence.User;

public interface UserService {
    User getCurrentUser();

    void setCurrentUser(User user);
}
