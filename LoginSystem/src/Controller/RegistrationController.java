package controller;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.DataStore;
import model.User;

import java.util.ArrayList;
import java.util.List;

public class RegistrationController {

    // References to the primary stage, user roles, invitation code, username, and password
    private Stage primaryStage;
    private List<String> roles;
    private String invitationCode;
    private String username;
    private String password;

    // Constructor for registration with roles and invitation code
    public RegistrationController(Stage primaryStage, List<String> roles, String invitationCode) {
        this.primaryStage = primaryStage;
        this.roles = roles;
        this.invitationCode = invitationCode;
    }

    // Constructor for registration without predefined roles (e.g., direct registration)
    public RegistrationController(Stage primaryStage, String username, String password) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.password = password;
    }

    // Displays the registration page with predefined roles (using an invitation code)
    public void showRegistrationPageWithRoles() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        
        GridPane grid = new GridPane();
        grid.setVgap(8); grid.setHgap(10);
        grid.setPadding(new Insets(10, 0, 0, 10));

        TextField usernameField = new TextField(); usernameField.setPrefWidth(200);
        PasswordField passwordField = new PasswordField(); passwordField.setPrefWidth(200); 
        PasswordField confirmPasswordField = new PasswordField(); confirmPasswordField.setPrefWidth(200); 

        Label messageLabel = new Label();
        messageLabel.setPrefWidth(200); messageLabel.setMaxWidth(200);
        messageLabel.setWrapText(true);

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

        // Add UI components to the VBox layout
        vbox.getChildren().addAll(
                new Label("Username:"), usernameField,
                new Label("Password:"), passwordField,
                new Label("Confirm Password:"), confirmPasswordField,
                registerButton, messageLabel
        );

        primaryStage.setScene(new Scene(vbox, 300, 200));
    }

    // Displays the role selection page for registration when roles are not predefined
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

    // Method to validate that password and confirm password match and are not empty
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
