package model;

import java.time.LocalDateTime;
import java.util.*;

public class User {

    // User attributes
    private String username, password, firstName, middleName, lastName, preferredFirstName, oneTimePassword;
    private List<String> roles = new ArrayList<>(); // List of roles assigned to the user
    private boolean accountSetupComplete = false; // Indicates whether the account setup is complete
    private LocalDateTime passwordExpiry; // Expiry time for one-time password resets

    // Constructor to initialize username and password
    public User(String username, String password) {
        this.username = username;
        setPassword(password); // Set the user's password
    }

    // Constructor to initialize username, password, and roles
    public User(String username, String password, List<String> roles) {
        this(username, password); // Call the primary constructor
        this.roles.addAll(roles); // Add roles to the user's role list
    }

    // Getters and setters for user attributes

    // Get the username of the user
    public String getUsername() { return username; }

    // Get the password of the user
    public String getPassword() { return password; }

    // Get the roles assigned to the user
    public List<String> getRoles() { return roles; }

    // Check if the account setup is complete
    public boolean isAccountSetupComplete() { return accountSetupComplete; }

    // Set the user's password
    public void setPassword(String password) { this.password = password; }

    // Set the roles for the user
    public void setRoles(List<String> roles) { this.roles = roles; }

    // Set additional user details (e.g., names)
    public void setDetails(String email, String firstName, String middleName, String lastName, String preferredFirstName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.preferredFirstName = preferredFirstName;
    }

    // Mark the account setup as complete
    public void setAccountSetupComplete(boolean complete) { this.accountSetupComplete = complete; }

    // Get the preferred first name, or default to first name if not set
    public String getPreferredFirstNameOrDefault() {
        return (preferredFirstName == null || preferredFirstName.isEmpty()) ? firstName : preferredFirstName;
    }

    // Add a new role to the user if it does not already exist
    public void addRole(String role) {
        if (!roles.contains(role)) roles.add(role);
    }

    // Get the one-time password for password resets
    public String getOneTimePassword() { return oneTimePassword; }

    // Get the expiry time for the one-time password
    public LocalDateTime getPasswordExpiry() { return passwordExpiry; }

    // Set the one-time password and its expiry time
    public void setOneTimePassword(String oneTimePassword, LocalDateTime expiry) {
        this.oneTimePassword = oneTimePassword;
        this.passwordExpiry = expiry;
    }

    // Check if a password reset is required (OTP is set and not expired)
    public boolean isPasswordResetRequired() {
        return oneTimePassword != null && LocalDateTime.now().isBefore(passwordExpiry);
    }

    // Clear the one-time password and its expiry time
    public void clearPasswordReset() {
        this.oneTimePassword = null;
        this.passwordExpiry = null;
    }

    // Get the full name of the user
    public String getFullName() {
        return String.format("%s %s %s", firstName, middleName != null ? middleName : "", lastName).trim(); // Concatenate and trim names
    }

    // Nested Invitation class to represent invitations for users
    public static class Invitation {
        private List<String> roles; // Roles associated with the invitation

        // Constructor to initialize roles for the invitation
        public Invitation(List<String> roles) {
            this.roles = roles;
        }

        // Get the roles associated with the invitation
        public List<String> getRoles() {
            return roles;
        }
    }
}

