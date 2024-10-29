package controller;

import model.DatabaseHelper;
import model.DataStore;
import model.Invitation;
import model.User;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminTabs {
    // Static reference to DatabaseHelper instance for database operations
	private static DatabaseHelper databaseHelper;
	
    // Static initializer block to initialize the DatabaseHelper instance with error handling
    static {
        try {
            databaseHelper = DatabaseHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Helper method to create a VBox layout with padding
    private static VBox createVBoxWithPadding() {
    	VBox vbox = new VBox(10); 			// 10px spacing between elements
        vbox.setPadding(new Insets(10)); 	// 10px padding on all sides
        return vbox;
    }

    // Helper method to add a label and input field to a VBox
    private static void setupLabelAndField(VBox vbox, String labelText, Control field) {
        vbox.getChildren().addAll(new Label(labelText), field);
    }

    // Creates the UI layout for the "Add Article" tab with necessary input fields and a submit button
    public static VBox createAddArticleTab() {
        VBox vbox = createVBoxWithPadding();
        
        // Define text fields for article attributes
        TextField titleField = new TextField(), authorsField = new TextField(), keywordsField = new TextField();
        TextArea abstractField = new TextArea(), bodyField = new TextArea(), referencesField = new TextArea();
        Label messageLabel = new Label();

        Button addArticleButton = new Button("Add Article");
        addArticleButton.setOnAction(e -> {
            try {
                databaseHelper.addArticle(
                    titleField.getText().trim(), authorsField.getText().trim(),
                    abstractField.getText().trim(), keywordsField.getText().trim(),
                    bodyField.getText().trim(), referencesField.getText().trim()
                );
                messageLabel.setText("Article added successfully!");

                // Clear fields after submission
                List.of(titleField, authorsField, abstractField, keywordsField, bodyField, referencesField)
                        .forEach(field -> ((TextInputControl) field).clear());

            } catch (Exception ex) {
                messageLabel.setText("Error adding article: " + ex.getMessage());
            }
        });
        
        // Setting up labels and input fields for various article details
        setupLabelAndField(vbox, "Title:", titleField);
        setupLabelAndField(vbox, "Authors (comma-separated):", authorsField);
        setupLabelAndField(vbox, "Abstract:", abstractField);
        setupLabelAndField(vbox, "Keywords (comma-separated):", keywordsField);
        setupLabelAndField(vbox, "Body:", bodyField);
        setupLabelAndField(vbox, "References (comma-separated):", referencesField);
        
        vbox.getChildren().addAll(addArticleButton, messageLabel);

        return vbox;
    }

    // Creates the UI layout for the "List Articles" tab
    public static VBox createListArticlesTab() {
        VBox vbox = createVBoxWithPadding();
        ListView<String> articlesListView = new ListView<>(); // ListView to display article titles
        Label messageLabel = new Label();
        
        Button listArticlesButton = new Button("Refresh List");
        listArticlesButton.setOnAction(e -> {
            try {
                articlesListView.getItems().setAll(databaseHelper.listArticles());
            } catch (Exception ex) {
                messageLabel.setText("Error listing articles: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(listArticlesButton, articlesListView, messageLabel);
        return vbox;
    }
    
    // Creates the UI layout for viewing an article by ID
    public static VBox createViewArticleTab() {
        VBox vbox = createVBoxWithPadding();
        TextField articleIdField = new TextField();
        TextArea articleDetailsArea = new TextArea();
        articleDetailsArea.setEditable(false);
        Label messageLabel = new Label();
        
        Button viewArticleButton = new Button("View Article");
        viewArticleButton.setOnAction(e -> {
            try {
                int articleId = Integer.parseInt(articleIdField.getText().trim());
                articleDetailsArea.setText(databaseHelper.viewArticle(articleId));
            } catch (Exception ex) {
                messageLabel.setText("Error viewing article: " + ex.getMessage());
            }
        });

        setupLabelAndField(vbox, "Article ID:", articleIdField);
        vbox.getChildren().addAll(viewArticleButton, articleDetailsArea, messageLabel);
        return vbox;
    }

    // Creates the UI layout for deleting an article by ID
    public static VBox createDeleteArticleTab() {
        VBox vbox = createVBoxWithPadding();
        TextField articleIdField = new TextField();
        Label messageLabel = new Label();
        
        Button deleteArticleButton = new Button("Delete Article");
        deleteArticleButton.setOnAction(e -> {
            try {
                databaseHelper.deleteArticle(Integer.parseInt(articleIdField.getText().trim()));
                messageLabel.setText("Article deleted successfully!");
            } catch (Exception ex) {
                messageLabel.setText("Error deleting article: " + ex.getMessage());
            }
        });

        setupLabelAndField(vbox, "Article ID:", articleIdField);
        vbox.getChildren().addAll(deleteArticleButton, messageLabel);
        return vbox;
    }

    // Creates the UI layout for the "Backup Articles" tab
    public static VBox createBackupArticlesTab() {
        VBox vbox = createVBoxWithPadding();
        TextField backupFileField = new TextField();	
        Label messageLabel = new Label();
        
        Button backupButton = new Button("Backup Articles");
        backupButton.setOnAction(e -> {
            try {
                databaseHelper.backupArticles(backupFileField.getText().trim());
                messageLabel.setText("Backup completed successfully!");
            } catch (Exception ex) {
                messageLabel.setText("Error backing up articles: " + ex.getMessage());
            }
        });

        setupLabelAndField(vbox, "Backup File Name (e.g., backup.txt):", backupFileField);
        vbox.getChildren().addAll(backupButton, messageLabel);
        return vbox;
    }

    // Creates the UI layout for the "Restore Articles" tab
    public static VBox createRestoreArticlesTab() {
        VBox vbox = createVBoxWithPadding();
        TextField restoreFileField = new TextField();
        Label messageLabel = new Label();
        
        Button restoreButton = new Button("Restore Articles");
        restoreButton.setOnAction(e -> {
            try {
                databaseHelper.restoreArticles(restoreFileField.getText().trim());
                messageLabel.setText("Restore completed successfully!");
            } catch (Exception ex) {
                messageLabel.setText("Error restoring articles: " + ex.getMessage());
            }
        });

        setupLabelAndField(vbox, "Restore File Name (e.g., backup.txt):", restoreFileField);
        vbox.getChildren().addAll(restoreButton, messageLabel);
        return vbox;
    }

	
	private static void addLabelAndFields(VBox vbox, Label label, Control... fields) {
        vbox.getChildren().add(label);
        vbox.getChildren().addAll(fields);
    }

    private static void showMessage(Label messageLabel, String message) {
        messageLabel.setText(message);
    }

    // Creates the UI layout for the "Invite User" tab, allowing role selection for invitation
    public static VBox createInviteUserTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        // Checkbox options for selecting user roles in the invitation
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor");
        Label codeLabel = new Label(), messageLabel = new Label();
        Button generateCodeButton = new Button("Generate Invitation Code");
        generateCodeButton.setOnAction(e -> {
            List<String> roles = new ArrayList<>();
            if (studentCheckBox.isSelected()) roles.add("Student");
            if (instructorCheckBox.isSelected()) roles.add("Instructor");
            if (!roles.isEmpty()) {
                String code = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
                DataStore.getInstance().getInvitations().put(code, new Invitation(roles));
                codeLabel.setText("Invitation Code: " + code);
            } else showMessage(messageLabel, "Select at least one role.");
        });
        addLabelAndFields(vbox, new Label("  Select Roles for Invitation:"), studentCheckBox, instructorCheckBox, generateCodeButton, codeLabel, messageLabel);
        return vbox;
    }

    // Creates the UI layout for "Reset User Account" tab with fields to set a one-time password and expiry
    public static VBox createResetUserTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField usernameField = new TextField(), expiryTimeField = new TextField();
        DatePicker expiryDatePicker = new DatePicker();
        Label messageLabel = new Label();
        Button resetButton = new Button("Reset User Account");
        resetButton.setOnAction(e -> {
            User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());
            if (user != null) {
                // Generate a one-time password and set expiry date and time
                String oneTimePassword = UUID.randomUUID().toString().substring(0, 4);
                LocalDateTime expiryDateTime = expiryDatePicker.getValue().atTime(
                        Integer.parseInt(expiryTimeField.getText().split(":")[0]),
                        Integer.parseInt(expiryTimeField.getText().split(":")[1]));
                user.setOneTimePassword(oneTimePassword, expiryDateTime);
                showMessage(messageLabel, "One-time password set: " + oneTimePassword);
            } else showMessage(messageLabel, "User not found.");
        });
        addLabelAndFields(vbox, new Label("  Username:"), usernameField, new Label("  Expiry Date:"), expiryDatePicker, new Label("  Expiry Time (HH:MM):"), expiryTimeField, resetButton, messageLabel);
        return vbox;
    }
    
    
    // Creates the UI layout for "Delete User Account" tab, allowing user deletion with confirmation
    public static VBox createDeleteUserTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField usernameField = new TextField();
        Label messageLabel = new Label();
        Button deleteButton = new Button("Delete User Account");
        deleteButton.setOnAction(e -> {
            User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());
            if (user != null) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        DataStore.getInstance().getUserList().remove(user);
                        showMessage(messageLabel, "User account deleted.");
                    }
                });
            } else showMessage(messageLabel, "User not found.");
        });
        addLabelAndFields(vbox, new Label("  Username:"), usernameField, deleteButton, messageLabel);
        return vbox;
    }

    // Creates the UI layout for "List Users" tab, displaying a refreshable list of users
    public static VBox createListUsersTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        ListView<String> userListView = new ListView<>();
        Button refreshButton = new Button("Refresh List");
        refreshButton.setOnAction(e -> {
            userListView.getItems().clear();
            DataStore.getInstance().getUserList().forEach(user -> userListView.getItems().add(
                    "Username: " + user.getUsername() + ", Name: " + user.getFullName() + ", Roles: " + String.join(", ", user.getRoles())));
        });
        vbox.getChildren().addAll(refreshButton, userListView);
        return vbox;
    }

    // Creates the UI layout for "Manage Roles" tab to assign roles to a specified user
    public static VBox createManageRolesTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField usernameField = new TextField();
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor"), adminCheckBox = new CheckBox("Administrator");
        Label messageLabel = new Label();
        Button updateRolesButton = new Button("Update Roles");
        updateRolesButton.setOnAction(e -> {
            User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());
            if (user != null) {
                List<String> roles = new ArrayList<>();
                if (studentCheckBox.isSelected()) roles.add("Student");
                if (instructorCheckBox.isSelected()) roles.add("Instructor");
                if (adminCheckBox.isSelected()) roles.add("Administrator");
                if (!roles.isEmpty()) {
                    user.setRoles(roles);
                    showMessage(messageLabel, "Roles updated.");
                } else showMessage(messageLabel, "Select at least one role.");
            } else showMessage(messageLabel, "User not found.");
        });
        addLabelAndFields(vbox, new Label("  Username:"), usernameField, new Label("  Assign Roles:"), studentCheckBox, instructorCheckBox, adminCheckBox, updateRolesButton, messageLabel);
        return vbox;
    }
}
