package utils;

import model.User;

public class UserSession {
    private static User loggedInUser;

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    public static void clearSession() {
        loggedInUser = null;
    }
}
