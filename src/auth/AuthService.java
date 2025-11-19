package auth;

import model.User;
import repository.IRepository;

public class AuthService implements IAuth {

    // Association with the repository interface
    private final IRepository repository;

    public AuthService(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public User login(String userID, String password) {
        User user = repository.getUserByID(userID);

        if (user != null && user.checkPass(password)) {
            System.out.println("Login Successful for user: " + user.getUserID());
            return user;
        }
        System.out.println("Login Failed: Invalid ID or password.");
        return null;
    }
}