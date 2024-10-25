package controller;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.User;

public class HomeController {

    private Stage primaryStage;
    private User user;
    private String role;

    public HomeController(Stage primaryStage, User user, String role) {
        this.primaryStage = primaryStage;
        this.user = user;
        this.role = role;
    }

    public void showHomePage() {
        VBox vbox = new VBox(10);
        vbox.getChildren().add(new Label("Welcome, " + role + " " + user.getPreferredFirstNameOrDefault() + "!"));
        if ("Administrator".equals(role)) {
            TabPane adminTabs = new TabPane();
            adminTabs.getTabs().addAll(
            		new Tab("Add Article", AdminTabs.createAddArticleUserTab()),
//            		new Tab("List Article", AdminTabs.createLtArticleUserTab()),
//            		new Tab("View Article", AdminTabs.createVwArticleUserTab()),
//            		new Tab("Delete Article", AdminTabs.createDelArticleUserTab()),
//            		new Tab("Backup Article", AdminTabs.createBackArticleUserTab()),
//            		new Tab("Restore Article", AdminTabs.createRestArticleUserTab()),
                    new Tab("Invite User", AdminTabs.createInviteUserTab()),
                    new Tab("Reset Account", AdminTabs.createResetUserTab()),
                    new Tab("Delete User", AdminTabs.createDeleteUserTab()),
                    new Tab("List Users", AdminTabs.createListUsersTab()),
                    new Tab("Manage Roles", AdminTabs.createManageRolesTab())
            );
            vbox.getChildren().add(adminTabs);
        }
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> {
            LoginController loginController = new LoginController(primaryStage);
            loginController.showLoginPage();
        });
        vbox.getChildren().add(logoutButton);
        primaryStage.setScene(new Scene(vbox, 500, 400));
    }
}
