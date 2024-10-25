package controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class RoleSelectionController {

    private Stage primaryStage;
    private User user;

    public RoleSelectionController(Stage primaryStage, User user) {
        this.primaryStage = primaryStage;
        this.user = user;
    }

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
                HomeController homeController = new HomeController(primaryStage, user, selectedRole.getText());
                homeController.showHomePage();
            }
        });
        vbox.getChildren().add(proceedButton);
        primaryStage.setScene(new Scene(vbox, 300, 200));
    }
}
