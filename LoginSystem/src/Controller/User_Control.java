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

    private Stage primaryStage;
    private User user;
    private String username;
    private String password;
    private List<String> roles;
    private String invitationCode;

    // Constructor for role selection and password reset
    public User_Control(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.user = user;
    }

    // Constructor for registration with invitation code
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

    // Show role selection page after login
    public void showRoleSelectionPage() {
        VBox vbox = new VBox(10);
        ToggleGroup roleGroup = new ToggleGroup();

        user.getRoles().forEach(role -> {
            RadioButton roleButton = new RadioButton(role);
            roleButton.setToggleGroup(roleGroup);
            vbox.getChildren().add(roleButton);
        });

        Button proceedButton = new Button("Proceed");
        proceedButton.setOnAction(e -> {
            RadioButton selectedRole = (RadioButton) roleGroup.getSelectedToggle();
            if (selectedRole != null) {
                new Dashboard(primaryStage, user, selectedRole.getText()).showHomePage();
            } else {
                new Alert(Alert.AlertType.ERROR, "Please select a role to proceed.").showAndWait();
            }
        });

        vbox.getChildren().add(proceedButton);
        primaryStage.setScene(new Scene(vbox, 288, 216));
    }

    // Show password reset page
    public void showPasswordResetPage() {
        VBox vbox = new VBox(10);
        PasswordField newPasswordField = new PasswordField(), confirmNewPasswordField = new PasswordField();
        Label messageLabel = new Label();
        Button resetButton = new Button("Reset Password");

        resetButton.setOnAction(e -> {
            String newPassword = newPasswordField.getText(), confirmNewPassword = confirmNewPasswordField.getText();
            if (newPassword.equals(confirmNewPassword) && !newPassword.isEmpty()) {
                user.setPassword(newPassword);
                user.clearPasswordReset();
                new Auth_Control(primaryStage).showLoginPage();
            } else {
                messageLabel.setText("Passwords do not match or are empty.");
            }
        });

        vbox.getChildren().addAll(new Label("Enter New Password:"), newPasswordField,
                new Label("Confirm New Password:"), confirmNewPasswordField, resetButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 288, 216));
    }

    // Show registration page with roles from invitation
    public void showRegistrationPageWithRoles() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField(), confirmPasswordField = new PasswordField();
        Label messageLabel = new Label();
        Button registerButton = new Button("Register");

        registerButton.setOnAction(e -> {
            if (validatePassword(passwordField.getText(), confirmPasswordField.getText(), messageLabel)) {
                User newUser = new User(usernameField.getText().trim(), passwordField.getText(), roles);
                DataStore.getInstance().getUserList().add(newUser);
                DataStore.getInstance().getInvitations().remove(invitationCode);
                new Auth_Control(primaryStage).showLoginPage();
            }
        });

        vbox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Confirm Password:"), confirmPasswordField,
                registerButton, messageLabel
        );

        primaryStage.setScene(new Scene(vbox, 288, 216));
    }

    // Show role selection page during registration
    public void showRoleSelectionForRegistration() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor");
        Label messageLabel = new Label();
        Button registerButton = new Button("Register");

        registerButton.setOnAction(e -> {
            List<String> selectedRoles = new ArrayList<>();
            if (studentCheckBox.isSelected()) selectedRoles.add("Student");
            if (instructorCheckBox.isSelected()) selectedRoles.add("Instructor");
            if (!selectedRoles.isEmpty()) {
                User newUser = new User(username, password, selectedRoles);
                DataStore.getInstance().getUserList().add(newUser);
                new Auth_Control(primaryStage).showLoginPage();
            } else {
                messageLabel.setText("Please select at least one role.");
            }
        });

        vbox.getChildren().addAll(new Label("Select your roles:"), studentCheckBox, instructorCheckBox, registerButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 288, 216));
    }

    // Password validation method
    private boolean validatePassword(String password, String confirmPassword, Label messageLabel) {
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
}
