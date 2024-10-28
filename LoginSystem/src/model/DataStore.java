package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {
    private static DataStore instance = null;
    private List<User> userList = new ArrayList<>();
    private Map<String, Invitation> invitations = new HashMap<>();

    private DataStore() {}

    public static DataStore getInstance() {
        if (instance == null) { instance = new DataStore(); }
        return instance;
    }

    public List<User> getUserList() { return userList; }
    public Map<String, Invitation> getInvitations() { return invitations; }

    public User findUserByUsername(String username) {
        return userList.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }
}
