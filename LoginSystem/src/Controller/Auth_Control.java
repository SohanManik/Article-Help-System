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

    private Stage primaryStage;
    private TextField usernameField = new TextField(), invitationCodeField = new TextField();
    private PasswordField passwordField = new PasswordField(), confirmPasswordField = new PasswordField();
    private Label messageLabel = new Label();

    public Auth_Control(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void showLoginPage() {
        GridPane grid = new GridPane();
        grid.setVgap(8); grid.setHgap(10); grid.setPadding(new Insets(10));
        messageLabel.setPrefWidth(180); messageLabel.setWrapText(true);

        grid.addRow(0, new Label("Username:"), usernameField);
        grid.addRow(1, new Label("Password:"), passwordField);
        grid.addRow(2, new Label("Confirm Password:"), confirmPasswordField);
        grid.addRow(3, new Label("Invitation Code:"), invitationCodeField);

        Button loginButton = new Button("Login / Register");
        loginButton.setOnAction(e -> handleLoginOrRegister());
        grid.addRow(4, loginButton, messageLabel);

        primaryStage.setScene(new Scene(grid, 400, 400));
        primaryStage.show();
    }

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
                        new AccountSetupController(user).showAccountSetupPage();
                    } else {
                        proceedAfterLogin(user);
                    }
                } else {
                    messageLabel.setText("Invalid username or password.");
                }
            } else if (validatePassword(password, confirmPassword)) {
                new User_Control(primaryStage, username, password).showRoleSelectionForRegistration();
            } else {
                messageLabel.setText("Invalid login or registration details.");
            }
        }
        clearFields();
    }

    private void handleInvitationCode(String code) {
        User.Invitation invitation = DataStore.getInstance().getInvitations().get(code);
        if (invitation != null) {
            new User_Control(primaryStage, invitation.getRoles(), code).showRegistrationPageWithRoles();
        } else {
            messageLabel.setText("Invalid invitation code.");
        }
    }

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

    private void registerAdmin(String username, String password) {
        User admin = new User(username, password);
        admin.addRole("Administrator");
        DataStore.getInstance().getUserList().add(admin);
        messageLabel.setText("Admin account created. Please log in again.");
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        invitationCodeField.clear();
    }

    void proceedAfterLogin(User user) {
        if (user.getRoles().size() > 1) {
            new User_Control(primaryStage, user).showRoleSelectionPage();
        } else {
            new Dashboard(primaryStage, user, user.getRoles().get(0)).showHomePage();
        }
    }

    private class AccountSetupController {

        private User user;

        public AccountSetupController(User user) {
            this.user = user;
        }

        public void showAccountSetupPage() {
            GridPane grid = new GridPane();
            grid.setVgap(8); grid.setHgap(10); grid.setPadding(new Insets(10));

            TextField emailField = new TextField(), firstNameField = new TextField(),
                    middleNameField = new TextField(), lastNameField = new TextField(),
                    preferredFirstNameField = new TextField();
            Label setupMessageLabel = new Label();
            Button finishSetupButton = new Button("Finish Setup");

            finishSetupButton.setOnAction(e -> {
                if (emailField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty()
                        || lastNameField.getText().trim().isEmpty()) {
                    setupMessageLabel.setText("Please fill in all required fields.");
                } else {
                    user.setDetails(emailField.getText(), firstNameField.getText(), middleNameField.getText(),
                            lastNameField.getText(), preferredFirstNameField.getText());
                    user.setAccountSetupComplete(true);
                    proceedAfterLogin(user);
                }
            });

            grid.addRow(0, new Label("Email:"), emailField);
            grid.addRow(1, new Label("First Name:"), firstNameField);
            grid.addRow(2, new Label("Middle Name:"), middleNameField);
            grid.addRow(3, new Label("Last Name:"), lastNameField);
            grid.addRow(4, new Label("Preferred First Name:"), preferredFirstNameField);
            grid.addRow(5, finishSetupButton, setupMessageLabel);

            primaryStage.setScene(new Scene(grid, 400, 400));
        }
    }
}