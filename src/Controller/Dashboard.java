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

    // Primary stage of the application
    private Stage primaryStage;

    // Logged-in user
    private User user;

    // Role of the logged-in user
    private String role;

    // Constructor initializes the stage, user, and role
    public Dashboard(Stage primaryStage, User user, String role) {
        this.primaryStage = primaryStage;
        this.user = user;
        this.role = role;
    }

    // Displays the main dashboard/home page
    public void showHomePage() {
        VBox vbox = new VBox(15); // Vertical layout with 15px spacing
        vbox.setPadding(new Insets(20)); // Padding around the layout
        vbox.setAlignment(Pos.TOP_CENTER); // Align content at the top center

        // Welcome label with personalized message
        Label welcomeLabel = new Label("Welcome, " + role + " " + user.getPreferredFirstNameOrDefault() + "!");
        welcomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        // Create role-specific tabs
        VBox tabs = createTabsForRole(role);
        if (tabs != null) {
            VBox.setVgrow(tabs, Priority.ALWAYS); // Allow tabs to grow vertically
        }

        // Logout button configuration
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
        logoutButton.setOnAction(e -> new Auth_Control(primaryStage).showLoginPage()); // Redirect to login page on click
        VBox.setMargin(logoutButton, new Insets(20, 0, 0, 0)); // Margin above the button

        // Add all components to the main layout
        vbox.getChildren().addAll(welcomeLabel, tabs, logoutButton);
        primaryStage.setScene(new Scene(vbox, 768, 576)); // Set the scene size
        primaryStage.setY(36); // Position the stage vertically
    }

    // Creates the tab layout for the user based on their role
    private VBox createTabsForRole(String role) {
        VBox buttonContainer = new VBox(0); // Container for tab buttons with no spacing
        StackPane contentArea = new StackPane(); // Area to display selected tab content
        HBox layout = new HBox(buttonContainer, contentArea); // Horizontal layout for tabs and content
        HBox.setHgrow(contentArea, Priority.ALWAYS); // Allow content area to grow horizontally

        // Get tabs for the specific role
        List<Pair<String, Node>> tabContents = getTabsForRole(role);
        Button firstButton = null; // To keep track of the first button

        // Create buttons for each tab
        for (Pair<String, Node> tab : tabContents) {
            Button tabButton = new Button(tab.getKey()); // Button with tab name
            tabButton.setMaxWidth(Double.MAX_VALUE); // Make button fill container width
            tabButton.setMinHeight(28); // Set a minimum height for the button
            tabButton.setStyle("-fx-border-color: #dcd9d1 ; -fx-border-width: 1 0 1 0;"); // Styling for button borders
            tabButton.setTextFill(javafx.scene.paint.Color.BLACK); // Text color

            // Set action for the tab button
            tabButton.setOnAction(e -> {
                contentArea.getChildren().setAll(tab.getValue()); // Show the corresponding tab content
                buttonContainer.getChildren().forEach(node -> 
                    ((Button)node).setStyle("-fx-border-color: #dcd9d1 ; -fx-border-width: 1 0 1 0;")); // Reset styles
                tabButton.setStyle("-fx-border-color: #dcd9d1 ; -fx-border-width: 1 0 1 0;"); // Highlight selected button
            });

            buttonContainer.getChildren().add(tabButton); // Add button to container
            if (firstButton == null) { firstButton = tabButton; } // Track the first button
        }

        // Show content of the first tab by default
        if (firstButton != null) {
            firstButton.fire(); // Simulate button click
        }

        return new VBox(layout); // Return the tab layout
    }

    // Returns a list of tabs for the given role
    private List<Pair<String, Node>> getTabsForRole(String role) {
        List<Pair<String, Node>> tabs = new ArrayList<>();

        // Define role-specific tabs
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
        return tabs; // Return the list of tabs
    }
}
