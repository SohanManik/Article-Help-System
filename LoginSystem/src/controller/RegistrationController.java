package controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DataStore;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    private Stage primaryStage;
    private List<String> roles;
    private String invitationCode;
    private String username;
    private String password;

    public RegistrationController(Stage primaryStage, List<String> roles, String invitationCode) {
        this.primaryStage = primaryStage;
        this.roles = roles;
        this.invitationCode = invitationCode;
    }

    public RegistrationController(Stage primaryStage, String username, String password) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.password = password;
    }

    public void showRegistrationPageWithRoles() {
        VBox vbox = new VBox(10);
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField(), confirmPasswordField = new PasswordField();
        Label messageLabel = new Label();
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            if (validatePassword(passwordField.getText(), confirmPasswordField.getText(), messageLabel)) {
                User newUser = new User(usernameField.getText().trim(), passwordField.getText(), roles);
                DataStore.getInstance().getUserList().add(newUser);
                DataStore.getInstance().getInvitations().remove(invitationCode);
                LoginController loginController = new LoginController(primaryStage);
                loginController.showLoginPage();
            }
        });
        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Password:"), passwordField,
                new Label("Confirm Password:"), confirmPasswordField, registerButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 300, 250));
    }

    public void showRoleSelectionForRegistration() {
        VBox vbox = new VBox(10);
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor");
        Label messageLabel = new Label();
        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            List<String> roles = new ArrayList<>();
            if (studentCheckBox.isSelected()) roles.add("Student");
            if (instructorCheckBox.isSelected()) roles.add("Instructor");
            if (!roles.isEmpty()) {
                User newUser = new User(username, password, roles);
                DataStore.getInstance().getUserList().add(newUser);
                LoginController loginController = new LoginController(primaryStage);
                loginController.showLoginPage();
            } else messageLabel.setText("Please select at least one role.");
        });
        vbox.getChildren().addAll(new Label("Select your roles:"), studentCheckBox, instructorCheckBox, registerButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 300, 200));
    }

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
