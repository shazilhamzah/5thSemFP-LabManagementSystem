package model;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userID;
    private String name;
    private String password;
    private Role role;

    public User(String userID, String name, String password, Role role) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    // Getters
    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    // Other Methods
    public boolean checkPass(String pass) {
        return this.password.equals(pass);
    }
}