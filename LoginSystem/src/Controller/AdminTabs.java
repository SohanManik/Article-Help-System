package Controller;

import model.DatabaseHelper;
import model.DataStore;
import model.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class AdminTabs {
    private static DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    private static VBox createVBox() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private static void addField(VBox vbox, String labelText, Control field) {
        vbox.getChildren().addAll(new Label(labelText), field);
    }

    private static void showMessage(Label label, String message) {
        label.setText(message);
    }

    public static VBox createAddArticleTab() {
        VBox vbox = createVBox();
        TextField titleField = new TextField(), authorsField = new TextField(), keywordsField = new TextField();
        TextArea abstractField = new TextArea(), bodyField = new TextArea(), referencesField = new TextArea();
        CheckBox encryptCheckBox = new CheckBox("Encrypt Article Content");
        Label messageLabel = new Label();
        Button addButton = new Button("Add Article");

        addButton.setOnAction(e -> {
            try {
                String title = titleField.getText().trim(), authors = authorsField.getText().trim(),
                       abstractText = abstractField.getText().trim(), keywords = keywordsField.getText().trim(),
                       body = bodyField.getText().trim(), references = referencesField.getText().trim();

                if (title.isEmpty() || authors.isEmpty() || body.isEmpty()) {
                    showMessage(messageLabel, "Title, Authors, and Body are required.");
                    return;
                }

                boolean encrypt = encryptCheckBox.isSelected();
                if (encrypt) body = DatabaseHelper.encryptContent(body);

                databaseHelper.addArticle(title, authors, abstractText, keywords, body, references, encrypt);

                showMessage(messageLabel, "Article added successfully!");
                titleField.clear(); authorsField.clear(); abstractField.clear();
                keywordsField.clear(); bodyField.clear(); referencesField.clear();
                encryptCheckBox.setSelected(false);
            } catch (Exception ex) {
                showMessage(messageLabel, "Error adding article: " + ex.getMessage());
            }
        });

        addField(vbox, "Title:", titleField);
        addField(vbox, "Authors (comma-separated):", authorsField);
        addField(vbox, "Abstract:", abstractField);
        addField(vbox, "Keywords (comma-separated):", keywordsField);
        addField(vbox, "Body:", bodyField);
        addField(vbox, "References (comma-separated):", referencesField);
        vbox.getChildren().addAll(encryptCheckBox, addButton, messageLabel);
        return vbox;
    }

    public static VBox createListArticlesTab() {
        VBox vbox = createVBox();
        ListView<String> articlesListView = new ListView<>();
        Label messageLabel = new Label();
        Button refreshButton = new Button("Refresh List");

        refreshButton.setOnAction(e -> {
            try {
                articlesListView.getItems().setAll(databaseHelper.listArticles());
            } catch (Exception ex) {
                showMessage(messageLabel, "Error listing articles: " + ex.getMessage());
            }
        });

        vbox.getChildren().addAll(refreshButton, articlesListView, messageLabel);
        return vbox;
    }

    public static VBox createViewArticleTab() {
        VBox vbox = createVBox();
        TextField articleIdField = new TextField();
        TextArea articleDetailsArea = new TextArea();
        articleDetailsArea.setEditable(false);
        Label messageLabel = new Label();
        Button viewButton = new Button("View Article");

        viewButton.setOnAction(e -> {
            try {
                int articleId = Integer.parseInt(articleIdField.getText().trim());
                articleDetailsArea.setText(databaseHelper.viewArticle(articleId));
            } catch (Exception ex) {
                showMessage(messageLabel, "Error viewing article: " + ex.getMessage());
            }
        });

        addField(vbox, "Article ID:", articleIdField);
        vbox.getChildren().addAll(viewButton, articleDetailsArea, messageLabel);
        return vbox;
    }

    public static VBox createDeleteArticleTab() {
        VBox vbox = createVBox();
        TextField articleIdField = new TextField();
        Label messageLabel = new Label();
        Button deleteButton = new Button("Delete Article");

        deleteButton.setOnAction(e -> {
            try {
                databaseHelper.deleteArticle(Integer.parseInt(articleIdField.getText().trim()));
                showMessage(messageLabel, "Article deleted successfully!");
            } catch (Exception ex) {
                showMessage(messageLabel, "Error deleting article: " + ex.getMessage());
            }
        });

        addField(vbox, "Article ID:", articleIdField);
        vbox.getChildren().addAll(deleteButton, messageLabel);
        return vbox;
    }

    public static VBox createBackupArticlesTab() {
        VBox vbox = createVBox();
        TextField backupFileField = new TextField();
        Label messageLabel = new Label();
        Button backupButton = new Button("Backup Articles");

        backupButton.setOnAction(e -> {
            try {
                databaseHelper.backupArticles(backupFileField.getText().trim());
                showMessage(messageLabel, "Backup completed successfully!");
            } catch (Exception ex) {
                showMessage(messageLabel, "Error backing up articles: " + ex.getMessage());
            }
        });

        addField(vbox, "Backup File Name (e.g., backup.txt):", backupFileField);
        vbox.getChildren().addAll(backupButton, messageLabel);
        return vbox;
    }

    public static VBox createRestoreArticlesTab() {
        VBox vbox = createVBox();
        TextField restoreFileField = new TextField();
        Label messageLabel = new Label();
        Button restoreButton = new Button("Restore Articles");

        restoreButton.setOnAction(e -> {
            try {
                databaseHelper.restoreArticles(restoreFileField.getText().trim());
                showMessage(messageLabel, "Restore completed successfully!");
            } catch (Exception ex) {
                showMessage(messageLabel, "Error restoring articles: " + ex.getMessage());
            }
        });

        addField(vbox, "Restore File Name (e.g., backup.txt):", restoreFileField);
        vbox.getChildren().addAll(restoreButton, messageLabel);
        return vbox;
    }

    public static VBox createInviteUserTab() {
        VBox vbox = createVBox();
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor");
        Label codeLabel = new Label(), messageLabel = new Label();
        Button generateButton = new Button("Generate Invitation Code");

        generateButton.setOnAction(e -> {
            List<String> roles = new ArrayList<>();
            if (studentCheckBox.isSelected()) roles.add("Student");
            if (instructorCheckBox.isSelected()) roles.add("Instructor");
            if (!roles.isEmpty()) {
                String code = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
                DataStore.getInstance().getInvitations().put(code, new User.Invitation(roles));
                codeLabel.setText("Invitation Code: " + code);
            } else {
                showMessage(messageLabel, "Select at least one role.");
            }
        });

        vbox.getChildren().addAll(new Label("Select Roles for Invitation:"), studentCheckBox, instructorCheckBox, generateButton, codeLabel, messageLabel);
        return vbox;
    }

    public static VBox createResetUserTab() {
        VBox vbox = createVBox();
        TextField usernameField = new TextField(), expiryTimeField = new TextField();
        DatePicker expiryDatePicker = new DatePicker();
        Label messageLabel = new Label();
        Button resetButton = new Button("Reset User Account");

        resetButton.setOnAction(e -> {
            User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());
            if (user != null) {
                String otp = UUID.randomUUID().toString().substring(0, 4);
                LocalDateTime expiry = expiryDatePicker.getValue().atTime(
                        Integer.parseInt(expiryTimeField.getText().split(":")[0]),
                        Integer.parseInt(expiryTimeField.getText().split(":")[1]));
                user.setOneTimePassword(otp, expiry);
                showMessage(messageLabel, "One-time password set: " + otp);
            } else {
                showMessage(messageLabel, "User not found.");
            }
        });

        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Expiry Date:"), expiryDatePicker, new Label("Expiry Time (HH:MM):"), expiryTimeField, resetButton, messageLabel);
        return vbox;
    }

    public static VBox createDeleteUserTab() {
        VBox vbox = createVBox();
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
            } else {
                showMessage(messageLabel, "User not found.");
            }
        });

        vbox.getChildren().addAll(new Label("Username:"), usernameField, deleteButton, messageLabel);
        return vbox;
    }

    public static VBox createListUsersTab() {
        VBox vbox = createVBox();
        ListView<String> userListView = new ListView<>();
        Button refreshButton = new Button("Refresh List");

        refreshButton.setOnAction(e -> {
            userListView.getItems().clear();
            DataStore.getInstance().getUserList().forEach(user -> {
                userListView.getItems().add("Username: " + user.getUsername() + ", Name: " + user.getFullName() + ", Roles: " + String.join(", ", user.getRoles()));
            });
        });

        vbox.getChildren().addAll(refreshButton, userListView);
        return vbox;
    }

    public static VBox createManageRolesTab() {
        VBox vbox = createVBox();
        TextField usernameField = new TextField();
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor"), adminCheckBox = new CheckBox("Administrator");
        Label messageLabel = new Label();
        Button updateButton = new Button("Update Roles");

        updateButton.setOnAction(e -> {
            User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());
            if (user != null) {
                List<String> roles = new ArrayList<>();
                if (studentCheckBox.isSelected()) roles.add("Student");
                if (instructorCheckBox.isSelected()) roles.add("Instructor");
                if (adminCheckBox.isSelected()) roles.add("Administrator");

                if (!roles.isEmpty()) {
                    user.setRoles(roles);
                    showMessage(messageLabel, "Roles updated.");
                } else {
                    showMessage(messageLabel, "Select at least one role.");
                }
            } else {
                showMessage(messageLabel, "User not found.");
            }
        });

        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Assign Roles:"), studentCheckBox, instructorCheckBox, adminCheckBox, updateButton, messageLabel);
        return vbox;
    }

    public static VBox createManageGroupsTab() {
        // Layout setup
        VBox vbox = createVBox();
        TextField groupNameField = new TextField();
        TextField usernameField = new TextField();
        TextField articleIdField = new TextField();

        // Group type radio buttons
        RadioButton specialGroupRadio = new RadioButton("Special Group");
        RadioButton generalGroupRadio = new RadioButton("General Group");
        ToggleGroup groupTypeToggle = new ToggleGroup();
        specialGroupRadio.setToggleGroup(groupTypeToggle);
        generalGroupRadio.setToggleGroup(groupTypeToggle);

        // User rights radio buttons
        RadioButton adminRightsRadio = new RadioButton("Grant Admin Rights");
        RadioButton viewRightsRadio = new RadioButton("Grant View Rights");
        ToggleGroup userRightsToggle = new ToggleGroup();
        adminRightsRadio.setToggleGroup(userRightsToggle);
        viewRightsRadio.setToggleGroup(userRightsToggle);

        // Buttons
        Button addToGroupButton = new Button("Add Article/User to Group");
        Button deleteGroupButton = new Button("Delete Group");
        Button removeUserButton = new Button("Remove User from Group");

        Label messageLabel = new Label();

        // Event handlers for buttons
        addToGroupButton.setOnAction(e -> {
            try {
                String groupName = groupNameField.getText().trim();
                String username = usernameField.getText().trim();
                String articleIdStr = articleIdField.getText().trim();
                boolean isSpecialGroup = specialGroupRadio.isSelected();
                boolean grantAdminRights = adminRightsRadio.isSelected();
                boolean grantViewRights = viewRightsRadio.isSelected();

                if (groupName.isEmpty()) {
                    showMessage(messageLabel, "Group Name cannot be empty.");
                    return;
                }

                // Create group if it doesn't exist
                String groupId = null;
                try {
                    groupId = databaseHelper.getGroupIdByName(groupName);
                } catch (SQLException ex) {
                    databaseHelper.createGroup(groupName, isSpecialGroup);
                    groupId = databaseHelper.getGroupIdByName(groupName);
                    showMessage(messageLabel, "New group created: " + groupName);
                }

                // Add user to group
                if (!username.isEmpty()) {
                    String role = grantAdminRights ? "Admin" : (grantViewRights ? "Viewer" : "");
                    if (role.isEmpty()) {
                        showMessage(messageLabel, "Select a role for the user.");
                        return;
                    }
                    databaseHelper.addUserToGroup(groupId, username, role);
                    showMessage(messageLabel, "User added to group: " + username);
                }

                // Add article to group
                if (!articleIdStr.isEmpty()) {
                    try {
                        int articleId = Integer.parseInt(articleIdStr);
                        databaseHelper.addArticleToGroup(groupId, articleId, grantViewRights);
                        showMessage(messageLabel, "Article added to group: " + articleId);
                    } catch (NumberFormatException ex) {
                        showMessage(messageLabel, "Invalid Article ID.");
                    }
                }
            } catch (Exception ex) {
                showMessage(messageLabel, "Error: " + ex.getMessage());
            }
        });

        deleteGroupButton.setOnAction(e -> {
            try {
                String groupName = groupNameField.getText().trim();
                if (groupName.isEmpty()) {
                    showMessage(messageLabel, "Group Name cannot be empty.");
                    return;
                }
                String groupId = databaseHelper.getGroupIdByName(groupName);
                databaseHelper.deleteGroup(groupId);
                showMessage(messageLabel, "Group deleted: " + groupName);
            } catch (SQLException ex) {
                showMessage(messageLabel, "Error deleting group: " + ex.getMessage());
            }
        });

        removeUserButton.setOnAction(e -> {
            try {
                String groupName = groupNameField.getText().trim();
                String username = usernameField.getText().trim();

                if (groupName.isEmpty() || username.isEmpty()) {
                    showMessage(messageLabel, "Group Name and Username cannot be empty.");
                    return;
                }

                String groupId = databaseHelper.getGroupIdByName(groupName);
                if (databaseHelper.deleteUserFromGroup(groupId, username)) {
                    showMessage(messageLabel, "User removed from group: " + username);
                } else {
                    showMessage(messageLabel, "User not found in group.");
                }
            } catch (SQLException ex) {
                showMessage(messageLabel, "Error removing user: " + ex.getMessage());
            }
        });

        // Add fields and buttons to the layout
        vbox.getChildren().addAll(
            new Label("Group Name:"), groupNameField, specialGroupRadio, generalGroupRadio,
            new Label("Username:"), usernameField, adminRightsRadio, viewRightsRadio,
            new Label("Article ID:"), articleIdField,
            addToGroupButton, deleteGroupButton, removeUserButton,
            messageLabel
        );

        return vbox;
    }

    public static VBox createViewGroupUsersTab() {
        VBox vbox = createVBox();
        TextField groupNameField = new TextField();
        Label messageLabel = new Label();
        TableView<Map<String, String>> userTable = new TableView<>();
        Button viewUsersButton = new Button("View Group Users");

        // Table Columns
        String[][] columns = {{"Username", "username"}, {"Role", "role"}, {"Can View", "canView"}, {"Can Admin", "canAdmin"}};
        for (String[] col : columns) {
            TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(col[0]);
            tableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(col[1])));
            userTable.getColumns().add(tableColumn);
        }

        viewUsersButton.setOnAction(e -> {
            try {
                String groupName = groupNameField.getText().trim();
                if (groupName.isEmpty()) throw new IllegalArgumentException("Group name cannot be empty.");
                String groupId = databaseHelper.getGroupIdByName(groupName);
                userTable.getItems().setAll(databaseHelper.getUsersInGroup(groupId));
                showMessage(messageLabel, "Users loaded.");
            } catch (Exception ex) {
                showMessage(messageLabel, "Error: " + ex.getMessage());
                userTable.getItems().clear();
            }
        });

        // GUI Layout
        vbox.getChildren().addAll(
            new Label("Group Name:"), groupNameField, viewUsersButton,userTable, messageLabel
        );

        return vbox;
    }

    public static VBox createViewArticlesInGroupTab() {
        VBox vbox = createVBox();
        TextField groupNameField = new TextField();
        TextField usernameField = new TextField();
        Label messageLabel = new Label();
        TableView<Map<String, String>> articlesTable = new TableView<>();
        Button viewArticlesButton = new Button("View Articles");

        // Define and add columns
        TableColumn<Map<String, String>, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id")));
        
        TableColumn<Map<String, String>, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("title")));
        
        TableColumn<Map<String, String>, String> bodyColumn = new TableColumn<>("Body");
        bodyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("body")));
        
        articlesTable.getColumns().setAll(List.of(idColumn, titleColumn, bodyColumn));

        viewArticlesButton.setOnAction(e -> {
            try {
                String groupName = groupNameField.getText().trim();
                String username = usernameField.getText().trim();
                
                if (groupName.isEmpty() || username.isEmpty()) {
                    showMessage(messageLabel, "Group name and username cannot be empty.");
                    articlesTable.getItems().clear();
                    return;
                }
                
                String groupId = databaseHelper.getGroupIdByName(groupName);
                List<Map<String, String>> articles = databaseHelper.getArticlesInGroup(groupId, username);
                articlesTable.getItems().setAll(articles);
                showMessage(messageLabel, "Articles loaded.");
            } catch (Exception ex) {
                showMessage(messageLabel, "Error: " + ex.getMessage());
                articlesTable.getItems().clear();
            }
        });

        vbox.getChildren().addAll(
            new Label("Group Name:"), groupNameField,
            new Label("Username:"), usernameField,
            viewArticlesButton, articlesTable, messageLabel
        );
        return vbox;
    }
}
