package controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.User;

public class AccountSetupController {

    private Stage primaryStage;
    private User user;

    public AccountSetupController(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.user = user;
    }

    public void showAccountSetupPage() {
        GridPane grid = new GridPane();
        TextField emailField = new TextField(), firstNameField = new TextField(),
                middleNameField = new TextField(), lastNameField = new TextField(),
                preferredFirstNameField = new TextField();
        Label setupMessageLabel = new Label();
        Button finishSetupButton = new Button("Finish Setup");
        finishSetupButton.setOnAction(e -> {
            if (emailField.getText().trim().isEmpty() || firstNameField.getText().trim().isEmpty()
                    || lastNameField.getText().trim().isEmpty())
                setupMessageLabel.setText("Please fill in all required fields.");
            else {
                user.setDetails(emailField.getText(), firstNameField.getText(), middleNameField.getText(),
                        lastNameField.getText(), preferredFirstNameField.getText());
                user.setAccountSetupComplete(true);
                LoginController loginController = new LoginController(primaryStage);
                loginController.proceedAfterLogin(user);
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
