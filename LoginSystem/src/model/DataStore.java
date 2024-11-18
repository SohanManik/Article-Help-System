package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
	
    // Singleton instance to ensure a single instance of DataStore throughout the application
    private static DataStore instance = null;
    
    // List to store User objects and Map to store Invitation codes and associated details
    private List<User> userList = new ArrayList<>();
    private Map<String, User.Invitation> invitations = new HashMap<>();

    // Private constructor to prevent instantiation from outside the class
    private DataStore() {}

    // Static method to get the singleton instance of DataStore
    public static DataStore getInstance() {
        if (instance == null) { instance = new DataStore(); }
        return instance;
    }

    // Getter for the user list
    public List<User> getUserList() { return userList; }
    
    // Getter for the invitations map
    public Map<String, User.Invitation> getInvitations() { return invitations; }

    // Method to find a user by username in the user list; returns null if user is not found
    public User findUserByUsername(String username) {
        return userList.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }
}
