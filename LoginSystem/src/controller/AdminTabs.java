package controller;

import model.DatabaseHelper;
import model.DataStore;
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
        try { databaseHelper = DatabaseHelper.getInstance();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static VBox createVBoxWithPadding() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private static void setupLabelAndField(VBox vbox, String labelText, Control field) {
        vbox.getChildren().addAll(new Label(labelText), field);
    }

    private static void addLabelAndFields(VBox vbox, Label label, Control... fields) {
        vbox.getChildren().add(label);
        vbox.getChildren().addAll(fields);
    }

    private static void showMessage(Label messageLabel, String message) {
        messageLabel.setText(message);
    }

//    public static VBox createAddArticleTab() {
//        VBox vbox = createVBoxWithPadding();
//        TextField titleField = new TextField(), authorsField = new TextField(), keywordsField = new TextField();
//        TextArea abstractField = new TextArea(), bodyField = new TextArea(), referencesField = new TextArea();
//        Label messageLabel = new Label();
//        Button addArticleButton = new Button("Add Article");
//        addArticleButton.setOnAction(e -> {
//            try {
//                databaseHelper.addArticle(
//                    titleField.getText().trim(), authorsField.getText().trim(),
//                    abstractField.getText().trim(), keywordsField.getText().trim(),
//                    bodyField.getText().trim(), referencesField.getText().trim()
//                );
//                messageLabel.setText("Article added successfully!");
//                List.of(titleField, authorsField, abstractField, keywordsField, bodyField, referencesField)
//                        .forEach(field -> ((TextInputControl) field).clear());
//            } catch (Exception ex) {
//                messageLabel.setText("Error adding article: " + ex.getMessage());
//            }
//        });
//        setupLabelAndField(vbox, "Title:", titleField);
//        setupLabelAndField(vbox, "Authors (comma-separated):", authorsField);
//        setupLabelAndField(vbox, "Abstract:", abstractField);
//        setupLabelAndField(vbox, "Keywords (comma-separated):", keywordsField);
//        setupLabelAndField(vbox, "Body:", bodyField);
//        setupLabelAndField(vbox, "References (comma-separated):", referencesField);
//        vbox.getChildren().addAll(addArticleButton, messageLabel);
//        return vbox;
//    }
    
    public static VBox createAddArticleTab() {
        VBox vbox = createVBoxWithPadding();

        // Fields for article details
        TextField titleField = new TextField(), authorsField = new TextField(), keywordsField = new TextField();
        TextArea abstractField = new TextArea(), bodyField = new TextArea(), referencesField = new TextArea();
        CheckBox encryptCheckBox = new CheckBox("Encrypt Article Content");
        Label messageLabel = new Label();
        Button addArticleButton = new Button("Add Article");

        // Button Logic
        addArticleButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim();
                String authors = authorsField.getText().trim();
                String abstractText = abstractField.getText().trim();
                String keywords = keywordsField.getText().trim();
                String body = bodyField.getText().trim();
                String references = referencesField.getText().trim();

                // Encrypt content if checkbox is selected
                boolean encrypt = encryptCheckBox.isSelected();
                if (encrypt) {
                    body = DatabaseHelper.encryptContent(body);
                }

                databaseHelper.addArticle(title, authors, abstractText, keywords, body, references, encrypt);
                messageLabel.setText("Article added successfully!");
                
                // Clear fields after successful addition
                List.of(titleField, authorsField, abstractField, keywordsField, bodyField, referencesField)
                        .forEach(field -> ((TextInputControl) field).clear());
                encryptCheckBox.setSelected(false); // Reset encryption checkbox
            } catch (Exception ex) {
                messageLabel.setText("Error adding article: " + ex.getMessage());
            }
        });

        // Setup UI components
        setupLabelAndField(vbox, "Title:", titleField);
        setupLabelAndField(vbox, "Authors (comma-separated):", authorsField);
        setupLabelAndField(vbox, "Abstract:", abstractField);
        setupLabelAndField(vbox, "Keywords (comma-separated):", keywordsField);
        setupLabelAndField(vbox, "Body:", bodyField);
        setupLabelAndField(vbox, "References (comma-separated):", referencesField);
        vbox.getChildren().addAll(encryptCheckBox, addArticleButton, messageLabel);

        return vbox;
    }


    public static VBox createListArticlesTab() {
        VBox vbox = createVBoxWithPadding();
        ListView<String> articlesListView = new ListView<>();
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

    public static VBox createViewArticleTabForInstructor() {
        VBox vbox = createVBoxWithPadding();

        // Fields and controls for viewing an article
        TextField articleIdField = new TextField();
        TextArea articleDetailsArea = new TextArea();
        articleDetailsArea.setEditable(false);
        Label messageLabel = new Label();
        Button viewArticleButton = new Button("View Article");

        // Fields and controls for managing access rights
        CheckBox viewRightsCheckBox = new CheckBox("Enable/Disable for general group");
        CheckBox adminRightsCheckBox = new CheckBox("Enable/Disable for special access");
        Button updateAccessButton = new Button("Update Article Group");

        // View Article Button Logic
        viewArticleButton.setOnAction(e -> {
//            try {
//                int articleId = Integer.parseInt(articleIdField.getText().trim());
//                articleDetailsArea.setText(databaseHelper.viewArticle(articleId));
//                // Fetch current rights for the article and set checkbox states
//                String groupId = "article-" + articleId; // Assuming article ID maps to a group ID
//                viewRightsCheckBox.setSelected(databaseHelper.hasGroupViewRights(groupId));
//                adminRightsCheckBox.setSelected(databaseHelper.hasGroupAdminRights(groupId));
//            } catch (Exception ex) {
//                messageLabel.setText("Error viewing article: " + ex.getMessage());
//            }
        	try {
                int displayId = Integer.parseInt(articleIdField.getText().trim());
                int articleId = databaseHelper.getDatabaseIdForDisplayId(displayId);
                String groupId = "article-" + articleId;

                // Determine if the article is encrypted
                boolean isEncrypted = databaseHelper.isArticleEncrypted(articleId);

                // Enforce access rights rules based on encryption state
                if (isEncrypted) {
                    if (viewRightsCheckBox.isSelected()) {
                        messageLabel.setText("Cannot grant view rights to encrypted articles.");
                        return;
                    }
                    if (!adminRightsCheckBox.isSelected()) {
                        messageLabel.setText("Encrypted articles must have admin rights enabled.");
                        return;
                    }
                } else {
                    if (!viewRightsCheckBox.isSelected()) {
                        messageLabel.setText("Decrypted articles must have view rights enabled.");
                        return;
                    }
                    if (adminRightsCheckBox.isSelected()) {
                        messageLabel.setText("Cannot grant admin rights to decrypted articles.");
                        return;
                    }
                }

                // Update View Rights
                if (viewRightsCheckBox.isSelected()) {
                    databaseHelper.addGroupViewRights(groupId);
                } else {
                    databaseHelper.removeGroupViewRights(groupId);
                }

                // Update Admin Rights
                if (adminRightsCheckBox.isSelected()) {
                    databaseHelper.addGroupAdminRights(groupId);
                } else {
                    databaseHelper.removeGroupAdminRights(groupId);
                }

                messageLabel.setText("Group for the article updated successfully.");
            } catch (Exception ex) {
                messageLabel.setText("Error updating article group: " + ex.getMessage());
            }
        });

        // Add fields and controls to the VBox
        setupLabelAndField(vbox, "Article ID:", articleIdField);
        vbox.getChildren().addAll(viewArticleButton, articleDetailsArea);
        vbox.getChildren().addAll(
                new Label("Manage Article Group"),
                viewRightsCheckBox, adminRightsCheckBox,
                updateAccessButton, messageLabel
        );

        return vbox;
    }

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

    public static VBox createInviteUserTab() {
        VBox vbox = createVBoxWithPadding();
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor");
        Label codeLabel = new Label(), messageLabel = new Label();
        Button generateCodeButton = new Button("Generate Invitation Code");
        generateCodeButton.setOnAction(e -> {
            List<String> roles = new ArrayList<>();
            if (studentCheckBox.isSelected()) roles.add("Student");
            if (instructorCheckBox.isSelected()) roles.add("Instructor");
            if (!roles.isEmpty()) {
                String code = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
                DataStore.getInstance().getInvitations().put(code, new User.Invitation(roles));
                codeLabel.setText("Invitation Code: " + code);
            } else showMessage(messageLabel, "Select at least one role.");
        });
        addLabelAndFields(vbox, new Label("  Select Roles for Invitation:"), studentCheckBox, instructorCheckBox, generateCodeButton, codeLabel, messageLabel);
        return vbox;
    }

    public static VBox createResetUserTab() {
        VBox vbox = createVBoxWithPadding();
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
        VBox vbox = createVBoxWithPadding();
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
        VBox vbox = createVBoxWithPadding();
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
        VBox vbox = createVBoxWithPadding();
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
    
    public static VBox createManageAccessRightsTab() {
        VBox vbox = createVBoxWithPadding();
        TextField usernameField = new TextField();
        CheckBox viewRightsCheckBox = new CheckBox("General");
        CheckBox adminRightsCheckBox = new CheckBox("Special Access");
        Label messageLabel = new Label();
        Button updateAccessButton = new Button("Update Rights");

        updateAccessButton.setOnAction(e -> {
            try {
                String username = usernameField.getText().trim();
                boolean grantViewRights = viewRightsCheckBox.isSelected();
                boolean grantAdminRights = adminRightsCheckBox.isSelected();

                if (username.isEmpty()) {
                    showMessage(messageLabel, "Username is required.");
                    return;
                }

                User user = DataStore.getInstance().findUserByUsername(username);
                if (user != null) {
                    if (grantViewRights) { databaseHelper.addViewRights(user.getUsername());
                    } else { databaseHelper.removeViewRights(user.getUsername()); }

                    if (grantAdminRights) { databaseHelper.addAdminRights(user.getUsername());
                    } else { databaseHelper.removeAdminRights(user.getUsername()); }

                    showMessage(messageLabel, "Access rights updated successfully.");
                } else {
                    showMessage(messageLabel, "User not found.");
                }
            } catch (Exception ex) {
                showMessage(messageLabel, "Error updating access rights: " + ex.getMessage());
            }
        });

        addLabelAndFields(vbox,
                new Label("  Username:"), usernameField,
                viewRightsCheckBox, adminRightsCheckBox,
                updateAccessButton, messageLabel);

        return vbox;
    }

}
