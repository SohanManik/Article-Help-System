package Controller;

import java.util.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import model.User;

public class Dashboard {

    private Stage primaryStage;
    private User user;
    private String role;

    public Dashboard(Stage primaryStage, User user, String role) {
        this.primaryStage = primaryStage;
        this.user = user;
        this.role = role;
    }

    public void showHomePage() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);

        Label welcomeLabel = new Label("Welcome, " + role + " " + user.getPreferredFirstNameOrDefault() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        VBox tabs = createTabsForRole(role);
        if (tabs != null) {
            VBox.setVgrow(tabs, Priority.ALWAYS);
        }

        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> new Auth_Control(primaryStage).showLoginPage());
        VBox.setMargin(logoutButton, new Insets(20, 0, 0, 0));

        vbox.getChildren().addAll(welcomeLabel, tabs, logoutButton);
        primaryStage.setScene(new Scene(vbox, 768, 576));
        primaryStage.setY(36);
    }

    private VBox createTabsForRole(String role) {
        VBox buttonContainer = new VBox(0); // 0 spacing for seamless buttons
        StackPane contentArea = new StackPane();
        HBox layout = new HBox(buttonContainer, contentArea);
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        List<Pair<String, Node>> tabContents = getTabsForRole(role);
        Button firstButton = null;

        for (Pair<String, Node> tab : tabContents) {
            Button tabButton = new Button(tab.getKey());
            tabButton.setMaxWidth(Double.MAX_VALUE);
            tabButton.setMinHeight(28);
            tabButton.setStyle("-fx-border-color: #dcd9d1 ; -fx-border-width: 1 0 1 0;");
            tabButton.setTextFill(javafx.scene.paint.Color.BLACK);
            
            tabButton.setOnAction(e -> {
                contentArea.getChildren().setAll(tab.getValue());
                buttonContainer.getChildren().forEach(node -> 
                    ((Button)node).setStyle("-fx-border-color: #dcd9d1 ; -fx-border-width: 1 0 1 0;"));
                tabButton.setStyle("-fx-border-color: #dcd9d1 ; -fx-border-width: 1 0 1 0;");
            });

            buttonContainer.getChildren().add(tabButton);
            if (firstButton == null) { firstButton = tabButton; }
        }

        // Show first tab content by default
        if (firstButton != null) {
            firstButton.fire();
        }

        return new VBox(layout);
    }

    private List<Pair<String, Node>> getTabsForRole(String role) {
        List<Pair<String, Node>> tabs = new ArrayList<>();
        
        switch (role) {
            case "Administrator":
                tabs.addAll(Arrays.asList(
                    new Pair<>("Add Article", AdminTabs.createAddArticleTab()),
                    new Pair<>("List Articles", AdminTabs.createListArticlesTab()),
                    new Pair<>("Delete Article", AdminTabs.createDeleteArticleTab()),
                    new Pair<>("Backup Articles", AdminTabs.createBackupArticlesTab()),
                    new Pair<>("Restore Articles", AdminTabs.createRestoreArticlesTab()),
                    new Pair<>("Invite User", AdminTabs.createInviteUserTab()),
                    new Pair<>("Reset Account", AdminTabs.createResetUserTab()),
                    new Pair<>("Delete User", AdminTabs.createDeleteUserTab()),
                    new Pair<>("List Users", AdminTabs.createListUsersTab()),
                    new Pair<>("Manage Roles", AdminTabs.createManageRolesTab()),
                    new Pair<>("Manage Groups", AdminTabs.createManageGroupsTab()),
                    new Pair<>("View Group Users", AdminTabs.createViewGroupUsersTab()),
                    new Pair<>("View Articles in Group", AdminTabs.createViewArticlesInGroupTab())
                ));
                break;
            case "Instructor":
                tabs.addAll(Arrays.asList(
                    new Pair<>("Add Article", AdminTabs.createAddArticleTab()),
                    new Pair<>("List Articles", AdminTabs.createListArticlesTab()),
                    new Pair<>("View Articles", AdminTabs.createViewArticleTab()),
                    new Pair<>("Delete Article", AdminTabs.createDeleteArticleTab()),
                    new Pair<>("Backup Articles", AdminTabs.createBackupArticlesTab()),
                    new Pair<>("Restore Articles", AdminTabs.createRestoreArticlesTab()),
                    new Pair<>("Search Articles", StudentTabs.createSearchArticlesTab()),
                    new Pair<>("Manage Groups", AdminTabs.createManageGroupsTab()),
                    new Pair<>("Manage Group Users", AdminTabs.createViewGroupUsersTab()),
                    new Pair<>("View Articles in Group", AdminTabs.createViewArticlesInGroupTab())
                ));
                break;
            case "Student":
                tabs.addAll(Arrays.asList(
                    new Pair<>("Help System", StudentTabs.createHelpSystemTab()),
                    new Pair<>("Search Articles", StudentTabs.createSearchArticlesTab()),
                    new Pair<>("View Articles", AdminTabs.createViewArticleTab()),
                    new Pair<>("View Articles in Group", AdminTabs.createViewArticlesInGroupTab())
                ));
                break;
        }
        return tabs;
    }
}
