package controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class PasswordResetController {

    private Stage primaryStage;
    private User user;

    public PasswordResetController(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.user = user;
    }

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
                LoginController loginController = new LoginController(primaryStage);
                loginController.showLoginPage();
            } else messageLabel.setText("Passwords do not match or are empty.");
        });
        vbox.getChildren().addAll(new Label("Enter New Password:"), newPasswordField,
                new Label("Confirm New Password:"), confirmNewPasswordField, resetButton, messageLabel);
        primaryStage.setScene(new Scene(vbox, 300, 200));
    }
}
