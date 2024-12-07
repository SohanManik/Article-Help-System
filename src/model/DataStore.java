package model;

import java.util.*;

public class DataStore {

    // Singleton instance of DataStore
    private static DataStore instance = null;

    // User-related attributes
    private List<User> userList = new ArrayList<>(); // List of registered users
    private Map<String, User.Invitation> invitations = new HashMap<>(); // Map of invitation codes to user invitations

    // Help system-related attributes
    private static List<String> genericMessages = new ArrayList<>(); // List of generic help messages
    private static Map<String, List<String>> specificMessages = new HashMap<>(); // Map of specific queries to their messages

    // Private constructor to enforce singleton pattern
    private DataStore() {}

    // Singleton getInstance method to retrieve the single instance of DataStore
    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore(); // Initialize instance if not already created
        return instance; // Return the singleton instance
    }

    // User-related methods

    // Retrieves the list of registered users
    public List<User> getUserList() {
        return userList;
    }

    // Retrieves the map of invitation codes and their associated invitations
    public Map<String, User.Invitation> getInvitations() {
        return invitations;
    }

    // Finds and returns a user by their username, or null if not found
    public User findUserByUsername(String username) {
        return userList.stream()
                .filter(u -> u.getUsername().equals(username)) // Filter users by matching username
                .findFirst() // Get the first matching user
                .orElse(null); // Return null if no match is found
    }

    // Help system methods

    // Adds a generic help message to the list of generic messages
    public static void sendGenericMessage(String message) {
        genericMessages.add(message);
    }

    // Adds a specific help message for a given query to the specificMessages map
    public static void sendSpecificMessage(String query, String message) {
        specificMessages.computeIfAbsent(query, k -> new ArrayList<>()).add(message); // Create a new list if query is not present
    }

    // Retrieves all generic help messages
    public List<String> getGenericMessages() {
        return new ArrayList<>(genericMessages); // Return a copy of the list to prevent external modifications
    }

    // Retrieves all specific messages for a given query
    public List<String> getSpecificMessages(String query) {
        return specificMessages.getOrDefault(query, new ArrayList<>()); // Return messages or an empty list if query not found
    }

    // Clears all generic and specific help messages
    public void clearHelpMessages() {
        genericMessages.clear(); // Clear the generic messages list
        specificMessages.clear(); // Clear the specific messages map
    }

    // Additional utility methods can be added here as needed
}
