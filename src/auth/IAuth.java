package auth;

import model.User;

public interface IAuth {
    /**
     * Attempts to log in a user.
     * @param userID The user's ID.
     * @param password The user's password.
     * @return The User object if credentials are correct, or null otherwise.
     */
    User login(String userID, String password);
}