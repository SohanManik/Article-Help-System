package controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import model.DataStore;
import model.User;

public class LoginController {

    // Primary stage reference and input fields for login/registration
    private Stage primaryStage;
    private TextField usernameField = new TextField(), invitationCodeField = new TextField();
    private PasswordField passwordField = new PasswordField(), confirmPasswordField = new PasswordField();
    private Label messageLabel = new Label();

    // Constructor initializing the stage
    public LoginController(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Method to display the login page layout
    public void showLoginPage() {
        GridPane grid = new GridPane();
        grid.setVgap(8); grid.setHgap(10);			// Set vertical and horizontal spacing
        grid.setPadding(new Insets(10, 0, 0, 10));	// Padding for layout
        
        messageLabel.setPrefWidth(180); messageLabel.setMaxWidth(180);	// Set width for the message label
        messageLabel.setWrapText(true);									// Enable text wrapping for the message label
        
        // Add labels and input fields to the grid layout
        grid.addRow(0, new Label("Username:"), usernameField);
        grid.addRow(1, new Label("Password:"), passwordField);
        grid.addRow(2, new Label("Confirm Password:"), confirmPasswordField);
        grid.addRow(3, new Label("Invitation Code:"), invitationCodeField);
        
        // Button for logging in or registering, with action handler
        Button loginButton = new Button("Login / Register");
        loginButton.setOnAction(e -> handleLoginOrRegister());
        grid.addRow(4, loginButton, messageLabel);			// Add button and message label to grid

        primaryStage.setScene(new Scene(grid, 400, 400));	// Set the scene and show it on the stage
        primaryStage.show();
    }

    // Method to handle login or registration based on the provided details
    private void handleLoginOrRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String invitationCode = invitationCodeField.getText().trim();

        DataStore dataStore = DataStore.getInstance();

        if (!invitationCode.isEmpty()) {
            handleInvitationCode(invitationCode);
        } else if (dataStore.getUserList().isEmpty() && validatePassword(password, confirmPassword)) {
            registerAdmin(username, password);
        } else {
            User user = dataStore.findUserByUsername(username);
            if (user != null) {
                if (user.isPasswordResetRequired()) {
                    handlePasswordReset(user, password);
                } else if (user.getPassword().equals(password)) {
                    if (!user.isAccountSetupComplete()) {
                        AccountSetupController accountSetupController = new AccountSetupController(user);
                        accountSetupController.showAccountSetupPage();
                    } else {
                        proceedAfterLogin(user);
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } else if (validatePassword(password, confirmPassword)) {
                showRoleSelectionForRegistration(username, password);
            } else {
                messageLabel.setText("Invalid login or registration details.");
            }
        }
        clearFields();
    }

    // Method to handle registration via an invitation code
    private void handleInvitationCode(String code) {
        DataStore dataStore = DataStore.getInstance();
        User.Invitation invitation = dataStore.getInvitations().get(code);
        if (invitation != null) {
            RegistrationController registrationController = new RegistrationController(primaryStage, invitation.getRoles(), code);
            registrationController.showRegistrationPageWithRoles();
        } else {
            messageLabel.setText("Invalid invitation code.");
        }
    }

    // Method to handle password reset using a one-time password
    private void handlePasswordReset(User user, String oneTimePassword) {
        if (oneTimePassword.equals(user.getOneTimePassword()) && java.time.LocalDateTime.now().isBefore(user.getPasswordExpiry())) {
            UserController passwordResetController = new UserController(primaryStage, user);
            passwordResetController.showPasswordResetPage();
        } else {
            messageLabel.setText("Invalid or expired one-time password.");
        }
    }

    // Validates the entered password and confirmation password
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

    // Registers the first user as an Administrator if no users exist
    private void registerAdmin(String username, String password) {
        DataStore dataStore = DataStore.getInstance();
        User admin = new User(username, password);
        admin.addRole("Administrator");
        dataStore.getUserList().add(admin);
        messageLabel.setText("Admin account created. Please log in again.");
    }
    
    // Clears all input fields for username, password, confirm password, and invitation code
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        invitationCodeField.clear();
    }

    // Proceeds to the home page after successful login, allowing role selection if multiple roles exist
    void proceedAfterLogin(User user) {
        if (user.getRoles().size() > 1) {
            UserController roleSelectionController = new UserController(primaryStage, user);
            roleSelectionController.showRoleSelectionPage();
        } else {
            HomeController homeController = new HomeController(primaryStage, user, user.getRoles().get(0));
            homeController.showHomePage();
        }
    }
    
    // Displays role selection for a new user registration
    private void showRoleSelectionForRegistration(String username, String password) {
        RegistrationController registrationController = new RegistrationController(primaryStage, username, password);
        registrationController.showRoleSelectionForRegistration();
    }

    // Inner class to manage account setup for newly registered users
    private class AccountSetupController {

        private User user;

        public AccountSetupController(User user) {
            this.user = user;
        }

        public void showAccountSetupPage() {
            GridPane grid = new GridPane();
            grid.setVgap(8); grid.setHgap(10);
            grid.setPadding(new Insets(10, 0, 0, 10));

            // Define input fields for account details
            TextField emailField = new TextField(), firstNameField = new TextField(),
                    middleNameField = new TextField(), lastNameField = new TextField(),
                    preferredFirstNameField = new TextField();
            Label setupMessageLabel = new Label();
            Button finishSetupButton = new Button("Finish Setup");

            // Button action to save account setup details
            finishSetupButton.setOnAction(e -> {
                if (emailField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty()
                        || lastNameField.getText().trim().isEmpty()) {
                    setupMessageLabel.setText("Please fill in all required fields.");
                } else {
                    user.setDetails(emailField.getText(), firstNameField.getText(), middleNameField.getText(),
                            lastNameField.getText(), preferredFirstNameField.getText());
                    user.setAccountSetupComplete(true); // Mark account setup as complete
                    proceedAfterLogin(user);			// Proceed to home page after setup
                }
            });

            // Adding labels and input fields for account details to the grid layout
            grid.addRow(0, new Label("Email:"), emailField);
            grid.addRow(1, new Label("First Name:"), firstNameField);
            grid.addRow(2, new Label("Middle Name:"), middleNameField);
            grid.addRow(3, new Label("Last Name:"), lastNameField);
            grid.addRow(4, new Label("Preferred First Name:"), preferredFirstNameField);
            grid.addRow(5, finishSetupButton, setupMessageLabel);

            primaryStage.setScene(new Scene(grid, 400, 400)); // Set the account setup scene on the primary stage
        }
    }
}
