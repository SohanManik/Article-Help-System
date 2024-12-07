package Controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.*;
import model.DataStore;
import model.User;

public class User_Control {

    // The primary stage of the application
    private Stage primaryStage;

    // User object for operations like password reset or role selection
    private User user;

    // Username and password for new user registration
    private String username;
    private String password;

    // Roles and invitation code for registration with an invitation
    private List<String> roles;
    private String invitationCode;

    // Constructor for role selection and password reset operations
    public User_Control(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.user = user;
    }

    // Constructor for registration with roles and an invitation code
    public User_Control(Stage primaryStage, List<String> roles, String invitationCode) {
        this.primaryStage = primaryStage;
        this.roles = roles;
        this.invitationCode = invitationCode;
    }

    // Constructor for registration with username and password
    public User_Control(Stage primaryStage, String username, String password) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.password = password;
    }

    // Displays the role selection page after login
    public void showRoleSelectionPage() {
        VBox vbox = new VBox(10); // Layout with 10px spacing
        ToggleGroup roleGroup = new ToggleGroup(); // Group for role radio buttons

        // Create a radio button for each role and add to the layout
        user.getRoles().forEach(role -> {
            RadioButton roleButton = new RadioButton(role);
            roleButton.setToggleGroup(roleGroup);
            vbox.getChildren().add(roleButton);
        });

        // Proceed button to navigate based on the selected role
        Button proceedButton = new Button("Proceed");
        proceedButton.setOnAction(e -> {
            RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
            if (selectedRole != null) {
                new Dashboard(primaryStage, user, selectedRole.getText()).showHomePage(); // Navigate to dashboard
            } else {
                new Alert(Alert.AlertType.ERROR, "Please select a role to proceed.").showAndWait(); // Show error
            }
        });

        // Add the proceed button to the layout
        vbox.getChildren().add(proceedButton);
        primaryStage.setScene(new Scene(vbox, 288, 216)); // Set the scene size
    }

    // Displays the password reset page
    public void showPasswordResetPage() {
        VBox vbox = new VBox(10); // Layout with 10px spacing
        PasswordField newPasswordField = new PasswordField(); // Input for new password
        PasswordField confirmNewPasswordField = new PasswordField(); // Input to confirm password
        Label messageLabel = new Label(); // Label to display status messages
        Button resetButton = new Button("Reset Password"); // Button to trigger reset

        // Action for the reset button
        resetButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText();
            String confirmNewPassword = confirmNewPasswordField.getText();
            if (newPassword.equals(confirmNewPassword) && !newPassword.isEmpty()) {
                user.setPassword(newPassword); // Set the new password
                user.clearPasswordReset(); // Clear the password reset requirement
                new Auth_Control(primaryStage).showLoginPage(); // Redirect to login page
            } else {
                messageLabel.setText("Passwords do not match or are empty."); // Show error message
            }
        });

        // Add components to the layout
        vbox.getChildren().addAll(new Label("Enter New Password:"), newPasswordField,
                new Label("Confirm New Password:"), confirmNewPasswordField, resetButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 288, 216)); // Set the scene size
    }

    // Displays the registration page with roles provided by an invitation
    public void showRegistrationPageWithRoles() {
        VBox vbox = new VBox(10); // Layout with 10px spacing
        vbox.setPadding(new Insets(10)); // Padding around the layout

        TextField usernameField = new TextField(); // Input for username
        PasswordField passwordField = new PasswordField(); // Input for password
        PasswordField confirmPasswordField = new PasswordField(); // Input to confirm password
        Label messageLabel = new Label(); // Label to display status messages
        Button registerButton = new Button("Register"); // Button to trigger registration

        // Action for the register button
        registerButton.setOnAction(e -> {
            if (validatePassword(passwordField.getText(), confirmPasswordField.getText(), messageLabel)) {
                User newUser = new User(usernameField.getText().trim(), passwordField.getText(), roles); // Create new user
                DataStore.getInstance().getUserList().add(newUser); // Add user to datastore
                DataStore.getInstance().getInvitations().remove(invitationCode); // Remove invitation code
                new Auth_Control(primaryStage).showLoginPage(); // Redirect to login page
            }
        });

        // Add components to the layout
        vbox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Confirm Password:"), confirmPasswordField,
                registerButton, messageLabel
        );

        primaryStage.setScene(new Scene(vbox, 288, 216)); // Set the scene size
    }

    // Displays the role selection page during registration
    public void showRoleSelectionForRegistration() {
        VBox vbox = new VBox(10); // Layout with 10px spacing
        vbox.setPadding(new Insets(10)); // Padding around the layout
        CheckBox studentCheckBox = new CheckBox("Student"); // Checkbox for "Student" role
        CheckBox instructorCheckBox = new CheckBox("Instructor"); // Checkbox for "Instructor" role
        Label messageLabel = new Label(); // Label to display status messages
        Button registerButton = new Button("Register"); // Button to trigger registration

        // Action for the register button
        registerButton.setOnAction(e -> {
            List<String> selectedRoles = new ArrayList<>(); // List to store selected roles
            if (studentCheckBox.isSelected()) selectedRoles.add("Student");
            if (instructorCheckBox.isSelected()) selectedRoles.add("Instructor");
            if (!selectedRoles.isEmpty()) {
                User newUser = new User(username, password, selectedRoles); // Create new user
                DataStore.getInstance().getUserList().add(newUser); // Add user to datastore
                new Auth_Control(primaryStage).showLoginPage(); // Redirect to login page
            } else {
                messageLabel.setText("Please select at least one role."); // Show error message
            }
        });

        // Add components to the layout
        vbox.getChildren().addAll(new Label("Select your roles:"), studentCheckBox, instructorCheckBox, registerButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 288, 216)); // Set the scene size
    }

    // Validates the password fields
    private boolean validatePassword(String password, String confirmPassword, Label messageLabel) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Password fields cannot be empty."); // Show error message
            return false;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match."); // Show error message
            return false;
        }
        return true; // Validation successful
    }
}

