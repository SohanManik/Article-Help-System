package Controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.time.LocalDateTime;
import model.DataStore;
import model.User;

public class Auth_Control {

    // Primary stage of the application
    private Stage primaryStage;

    // Fields for user input
    private TextField usernameField = new TextField(), invitationCodeField = new TextField();
    private PasswordField passwordField = new PasswordField(), confirmPasswordField = new PasswordField();

    // Label to display messages
    private Label messageLabel = new Label();

    // Constructor initializes the primary stage
    public Auth_Control(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Displays the login or registration page
    public void showLoginPage() {
        GridPane grid = new GridPane();
        grid.setVgap(8); // Vertical spacing
        grid.setHgap(10); // Horizontal spacing
        grid.setPadding(new Insets(10)); // Padding around the grid

        // Configure message label
        messageLabel.setPrefWidth(180);
        messageLabel.setWrapText(true);

        // Add fields to the grid layout
        grid.addRow(0, new Label("Username:"), usernameField);
        grid.addRow(1, new Label("Password:"), passwordField);
        grid.addRow(2, new Label("Confirm Password:"), confirmPasswordField);
        grid.addRow(3, new Label("Invitation Code:"), invitationCodeField);

        // Button for login or registration
        Button loginButton = new Button("Login / Register");
        loginButton.setOnAction(e -> handleLoginOrRegister());
        grid.addRow(4, loginButton, messageLabel);

        // Set the scene and display the login page
        primaryStage.setScene(new Scene(grid, 400, 400));
        primaryStage.show();
    }

    // Handles login or registration logic based on user input
    private void handleLoginOrRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String invitationCode = invitationCodeField.getText().trim();

        DataStore dataStore = DataStore.getInstance();

        if (!invitationCode.isEmpty()) {
            // Handle registration using an invitation code
            handleInvitationCode(invitationCode);
        } else if (dataStore.getUserList().isEmpty() && validatePassword(password, confirmPassword)) {
            // Register the first user as an admin if the user list is empty
            registerAdmin(username, password);
        } else {
            // Handle login or registration for existing users
            User user = dataStore.findUserByUsername(username);
            if (user != null) {
                if (user.isPasswordResetRequired()) {
                    handlePasswordReset(user, password);
                } else if (user.getPassword().equals(password)) {
                    if (!user.isAccountSetupComplete()) {
                        new AccountSetupController(user).showAccountSetupPage();
                    } else {
                        proceedAfterLogin(user);
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } else if (validatePassword(password, confirmPassword)) {
                // Register a new user if the credentials are valid
                new User_Control(primaryStage, username, password).showRoleSelectionForRegistration();
            } else {
                messageLabel.setText("Invalid login or registration details.");
            }
        }
        clearFields(); // Clear input fields after handling
    }

    // Handles the logic for processing an invitation code
    private void handleInvitationCode(String code) {
        User.Invitation invitation = DataStore.getInstance().getInvitations().get(code);
        if (invitation != null) {
            new User_Control(primaryStage, invitation.getRoles(), code).showRegistrationPageWithRoles();
        } else {
            messageLabel.setText("Invalid invitation code.");
        }
    }

    // Handles password reset logic for a user
    private void handlePasswordReset(User user, String oneTimePassword) {
        if (user.isPasswordResetRequired() && user.getOneTimePassword().equals(oneTimePassword)) {
            if (LocalDateTime.now().isBefore(user.getPasswordExpiry())) {
                new User_Control(primaryStage, user).showPasswordResetPage();
            } else {
                messageLabel.setText("One-time password has expired.");
            }
        } else {
            messageLabel.setText("Invalid one-time password.");
        }
    }

    // Validates the password and confirmation password fields
    private boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Password fields cannot be empty.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return false;
        }
        return true;
    }

    // Registers the first user as an administrator
    private void registerAdmin(String username, String password) {
        User admin = new User(username, password);
        admin.addRole("Administrator");
        DataStore.getInstance().getUserList().add(admin);
        messageLabel.setText("Admin account created. Please log in again.");
    }

    // Clears all input fields
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        invitationCodeField.clear();
    }

    // Proceeds to the appropriate dashboard or role selection page after login
    void proceedAfterLogin(User user) {
        if (user.getRoles().size() > 1) {
            new User_Control(primaryStage, user).showRoleSelectionPage();
        } else {
            new Dashboard(primaryStage, user, user.getRoles().get(0)).showHomePage();
        }
    }

    // Inner class to handle the account setup process for new users
    private class AccountSetupController {

        private User user;

        // Constructor initializes the user
        public AccountSetupController(User user) {
            this.user = user;
        }

        // Displays the account setup page
        public void showAccountSetupPage() {
            GridPane grid = new GridPane();
            grid.setVgap(8); // Vertical spacing
            grid.setHgap(10); // Horizontal spacing
            grid.setPadding(new Insets(10)); // Padding around the grid

            // Fields for account setup details
            TextField emailField = new TextField(), firstNameField = new TextField(),
                    middleNameField = new TextField(), lastNameField = new TextField(),
                    preferredFirstNameField = new TextField();

            // Label to display setup messages
            Label setupMessageLabel = new Label();

            // Button to complete the account setup process
            Button finishSetupButton = new Button("Finish Setup");

            // Action for the "Finish Setup" button
            finishSetupButton.setOnAction(e -> {
                if (emailField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty()
                        || lastNameField.getText().trim().isEmpty()) {
                    setupMessageLabel.setText("Please fill in all required fields.");
                } else {
                    // Save user details and mark the setup as complete
                    user.setDetails(emailField.getText(), firstNameField.getText(), middleNameField.getText(),
                            lastNameField.getText(), preferredFirstNameField.getText());
                    user.setAccountSetupComplete(true);
                    proceedAfterLogin(user);
                }
            });

            // Add fields and button to the grid layout
            grid.addRow(0, new Label("Email:"), emailField);
            grid.addRow(1, new Label("First Name:"), firstNameField);
            grid.addRow(2, new Label("Middle Name:"), middleNameField);
            grid.addRow(3, new Label("Last Name:"), lastNameField);
            grid.addRow(4, new Label("Preferred First Name:"), preferredFirstNameField);
            grid.addRow(5, finishSetupButton, setupMessageLabel);

            // Set the scene and display the account setup page
            primaryStage.setScene(new Scene(grid, 400, 400));
        }
    }
}
