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

	private static DatabaseHelper databaseHelper;

    static {
        try {
            databaseHelper = DatabaseHelper.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static VBox createAddArticleTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        TextField titleField = new TextField();
        TextField authorsField = new TextField();
        TextArea abstractField = new TextArea();
        TextField keywordsField = new TextField();
        TextArea bodyField = new TextArea();
        TextArea referencesField = new TextArea();
        Label messageLabel = new Label();
        Button addArticleButton = new Button("Add Article");

        addArticleButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String authors = authorsField.getText().trim();
                String abstractText = abstractField.getText().trim();
                String keywords = keywordsField.getText().trim();
                String body = bodyField.getText().trim();
                String references = referencesField.getText().trim();

                databaseHelper.addArticle(title, authors, abstractText, keywords, body, references);
                messageLabel.setText("Article added successfully!");

                // Clear fields after submission
                titleField.clear();
                authorsField.clear();
                abstractField.clear();
                keywordsField.clear();
                bodyField.clear();
                referencesField.clear();

            } catch (Exception ex) {
                messageLabel.setText("Error adding article: " + ex.getMessage());
            }
        });

        // Add all components to the VBox
        vbox.getChildren().addAll(
                new Label("Title:"), titleField,
                new Label("Authors (comma-separated):"), authorsField,
                new Label("Abstract:"), abstractField,
                new Label("Keywords (comma-separated):"), keywordsField,
                new Label("Body:"), bodyField,
                new Label("References (comma-separated):"), referencesField,
                addArticleButton,
                messageLabel
        );

        return vbox;
    }
	
	public static VBox createListArticlesTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        ListView<String> articlesListView = new ListView<>();
        Button listArticlesButton = new Button("Refresh List");
        Label messageLabel = new Label();

        listArticlesButton.setOnAction(e -> {
            try {
                articlesListView.getItems().clear();
                List<String> articles = databaseHelper.listArticles(); // Assuming this returns a list of articles
                articlesListView.getItems().addAll(articles);
            } catch (Exception ex) {
                messageLabel.setText("Error listing articles: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(listArticlesButton, articlesListView, messageLabel);
        return vbox;
    }

    public static VBox createViewArticleTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField articleIdField = new TextField();
        TextArea articleDetailsArea = new TextArea();
        articleDetailsArea.setEditable(false);
        Button viewArticleButton = new Button("View Article");
        Label messageLabel = new Label();

        viewArticleButton.setOnAction(e -> {
            try {
                int articleId = Integer.parseInt(articleIdField.getText().trim());
                String articleDetails = databaseHelper.viewArticle(articleId); // Assuming this returns article details as a string
                articleDetailsArea.setText(articleDetails);
            } catch (Exception ex) {
                messageLabel.setText("Error viewing article: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(new Label("Article ID:"), articleIdField, viewArticleButton, articleDetailsArea, messageLabel);
        return vbox;
    }

    public static VBox createDeleteArticleTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField articleIdField = new TextField();
        Button deleteArticleButton = new Button("Delete Article");
        Label messageLabel = new Label();

        deleteArticleButton.setOnAction(e -> {
            try {
                int articleId = Integer.parseInt(articleIdField.getText().trim());
                databaseHelper.deleteArticle(articleId);
                messageLabel.setText("Article deleted successfully!");
            } catch (Exception ex) {
                messageLabel.setText("Error deleting article: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(new Label("Article ID:"), articleIdField, deleteArticleButton, messageLabel);
        return vbox;
    }

    public static VBox createBackupArticlesTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField backupFileField = new TextField();
        Button backupButton = new Button("Backup Articles");
        Label messageLabel = new Label();

        backupButton.setOnAction(e -> {
            try {
                String backupFileName = backupFileField.getText().trim();
                databaseHelper.backupArticles(backupFileName);
                messageLabel.setText("Backup completed successfully!");
            } catch (Exception ex) {
                messageLabel.setText("Error backing up articles: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(new Label("Backup File Name (e.g., backup.txt):"), backupFileField, backupButton, messageLabel);
        return vbox;
    }

    public static VBox createRestoreArticlesTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
        TextField restoreFileField = new TextField();
        Button restoreButton = new Button("Restore Articles");
        Label messageLabel = new Label();

        restoreButton.setOnAction(e -> {
            try {
                String restoreFileName = restoreFileField.getText().trim();
                databaseHelper.restoreArticles(restoreFileName);
                messageLabel.setText("Restore completed successfully!");
            } catch (Exception ex) {
                messageLabel.setText("Error restoring articles: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(new Label("Restore File Name (e.g., backup.txt):"), restoreFileField, restoreButton, messageLabel);
        return vbox;
    }
	
	private static void addLabelAndFields(VBox vbox, Label label, Control... fields) {
        vbox.getChildren().add(label);
        vbox.getChildren().addAll(fields);
    }

    private static void showMessage(Label messageLabel, String message) {
        messageLabel.setText(message);
    }

    public static VBox createInviteUserTab() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        
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
