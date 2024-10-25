package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String username, password, email, firstName, middleName, lastName, preferredFirstName, oneTimePassword;
    private List<String> roles = new ArrayList<>();
    private boolean accountSetupComplete = false;
    private LocalDateTime passwordExpiry;

    public User(String username, String password) {
        this.username = username; this.password = password;
    }

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

    public void setDetails(String email, String firstName, String middleName, String lastName, String preferredFirstName) {
        this.email = email; this.firstName = firstName; this.middleName = middleName;
        this.lastName = lastName; this.preferredFirstName = preferredFirstName;
    }

    public void setAccountSetupComplete(boolean complete) { this.accountSetupComplete = complete; }

    public String getPreferredFirstNameOrDefault() {
        return (preferredFirstName == null || preferredFirstName.isEmpty()) ? firstName : preferredFirstName;
    }

    public void addRole(String role) {
        if (!roles.contains(role)) roles.add(role);
    }

    public String getOneTimePassword() { return oneTimePassword; }
    public LocalDateTime getPasswordExpiry() { return passwordExpiry; }

    public void setOneTimePassword(String oneTimePassword, LocalDateTime expiry) {
        this.oneTimePassword = oneTimePassword; this.passwordExpiry = expiry;
    }

    public boolean isPasswordResetRequired() {
        return oneTimePassword != null && LocalDateTime.now().isBefore(passwordExpiry);
    }

    public void clearPasswordReset() {
        this.oneTimePassword = null; this.passwordExpiry = null;
    }

    public String getFullName() {
        return String.format("%s %s %s", firstName, middleName != null ? middleName : "", lastName).trim();
    }
}
