package model;

import java.util.*;

public class DataStore {

    private static DataStore instance = null;
    
    // User-related attributes
    private List<User> userList = new ArrayList<>();
    private Map<String, User.Invitation> invitations = new HashMap<>();
    
    // Help system-related attributes
    private static List<String> genericMessages = new ArrayList<>();
    private static Map<String, List<String>> specificMessages = new HashMap<>();

    // Private constructor for singleton pattern
    private DataStore() {}

    // Singleton getInstance method
    public static DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }

    // User-related methods
    public List<User> getUserList() {
        return userList;
    }

    public Map<String, User.Invitation> getInvitations() {
        return invitations;
    }

    public User findUserByUsername(String username) {
        return userList.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    // Help system methods
    public static void sendGenericMessage(String message) {
        genericMessages.add(message);
    }

    public static void sendSpecificMessage(String query, String message) {
        specificMessages.computeIfAbsent(query, k -> new ArrayList<>()).add(message);
    }

    public List<String> getGenericMessages() {
        return new ArrayList<>(genericMessages);
    }

    public List<String> getSpecificMessages(String query) {
        return specificMessages.getOrDefault(query, new ArrayList<>());
    }

    public void clearHelpMessages() {
        genericMessages.clear();
        specificMessages.clear();
    }

    // Additional utility methods can be added here as needed
}