package is.hi.hbv601g.verzlunapp.services;

public class LogoutService {
    private final NetworkService networkService;
    private final UserService userService;

    public LogoutService(NetworkService networkService, UserService userService) {
        this.networkService = networkService;
        this.userService = userService;
    }

    public boolean logoutUser() {
        try {
            String response = networkService.post("/auth/logout", "{}");

            // Assume log out was successful if we got a response
            if (response != null) {
                userService.setCurrentUser(null);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
