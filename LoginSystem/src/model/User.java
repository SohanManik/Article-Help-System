package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    @SuppressWarnings("unused")
	private String username, password, email, firstName, middleName, lastName, preferredFirstName, oneTimePassword;
    private List<String> roles = new ArrayList<>();
    private boolean accountSetupComplete = false;
    private LocalDateTime passwordExpiry;

    // Constructor to initialize user with username and password
    public User(String username, String password) {
        this.username = username; this.password = password;
    }

    // Overloaded constructor to initialize user with username, password, and roles
    public User(String username, String password, List<String> roles) {
        this(username, password); this.roles.addAll(roles);
    }

    // Getters and setters...

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public List<String> getRoles() { return roles; }
    public boolean isAccountSetupComplete() { return accountSetupComplete; }
    
    public void setPassword(String password) { this.password = password; }
    public void setRoles(List<String> roles) { this.roles = roles; }

    // Sets user details including email, name parts, and preferred first name
    public void setDetails(String email, String firstName, String middleName, String lastName, String preferredFirstName) {
        this.email = email; this.firstName = firstName; this.middleName = middleName;
        this.lastName = lastName; this.preferredFirstName = preferredFirstName;
    }

    public void setAccountSetupComplete(boolean complete) { this.accountSetupComplete = complete; }

    // Returns the preferred first name if set, otherwise defaults to first name
    public String getPreferredFirstNameOrDefault() {
        return (preferredFirstName == null || preferredFirstName.isEmpty()) ? firstName : preferredFirstName;
    }

    // Adds a role to the user if it doesn't already exist in their roles list
    public void addRole(String role) {
        if (!roles.contains(role)) roles.add(role);
    }

    // Getters for one-time password and password expiry information
    public String getOneTimePassword() { return oneTimePassword; }
    public LocalDateTime getPasswordExpiry() { return passwordExpiry; }

    // Sets a one-time password and expiry for password reset
    public void setOneTimePassword(String oneTimePassword, LocalDateTime expiry) {
        this.oneTimePassword = oneTimePassword; this.passwordExpiry = expiry;
    }

    // Checks if a password reset is required based on one-time password and expiry
    public boolean isPasswordResetRequired() {
        return oneTimePassword != null && LocalDateTime.now().isBefore(passwordExpiry);
    }

    // Clears the one-time password and its expiry time, typically after reset
    public void clearPasswordReset() {
        this.oneTimePassword = null; this.passwordExpiry = null;
    }

    // Returns the full name of the user, formatted with first, middle, and last names
    public String getFullName() {
        return String.format("%s %s %s", firstName, middleName != null ? middleName : "", lastName).trim();
    }
}