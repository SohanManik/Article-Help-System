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
	// DatabaseHelper is used for interacting with the database. Singleton instance
	// is retrieved.
	private static DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

	// Utility method to create a VBox with standard padding and spacing.
	private static VBox createVBox() {
		VBox vbox = new VBox(10); // 10px spacing between elements
		vbox.setPadding(new Insets(10)); // 10px padding around the VBox
		return vbox;
	}

	// Utility method to add a label and a corresponding field (Control) to a VBox.
	private static void addField(VBox vbox, String labelText, Control field) {
		vbox.getChildren().addAll(new Label(labelText), field);
	}

	// Utility method to display a message in a Label.
	private static void showMessage(Label label, String message) {
		label.setText(message); // Sets the message text to the label
	}

	// Creates the "Add Article" tab UI and its functionality.
	public static VBox createAddArticleTab() {
		VBox vbox = createVBox(); // Standard VBox layout

		// Input fields for article details
		TextField titleField = new TextField(), authorsField = new TextField(), keywordsField = new TextField();
		TextArea abstractField = new TextArea(), bodyField = new TextArea(), referencesField = new TextArea();
		CheckBox encryptCheckBox = new CheckBox("Encrypt Article Content"); // Option to encrypt the article body
		Label messageLabel = new Label(); // Label to display messages
		Button addButton = new Button("Add Article"); // Button to trigger article addition

		// Define the behavior when the "Add Article" button is clicked
		addButton.setOnAction(e -> {
			try {
				// Retrieve and trim input values
				String title = titleField.getText().trim(), authors = authorsField.getText().trim(),
						abstractText = abstractField.getText().trim(), keywords = keywordsField.getText().trim(),
						body = bodyField.getText().trim(), references = referencesField.getText().trim();

				// Validate required fields
				if (title.isEmpty() || authors.isEmpty() || body.isEmpty()) {
					showMessage(messageLabel, "Title, Authors, and Body are required.");
					return;
				}

				// Check if encryption is enabled and encrypt the body if needed
				boolean encrypt = encryptCheckBox.isSelected();
				if (encrypt)
					body = DatabaseHelper.encryptContent(body);

				// Add the article to the database
				databaseHelper.addArticle(title, authors, abstractText, keywords, body, references, encrypt);

				// Clear fields and reset UI after successful addition
				showMessage(messageLabel, "Article added successfully!");
				titleField.clear();
				authorsField.clear();
				abstractField.clear();
				keywordsField.clear();
				bodyField.clear();
				referencesField.clear();
				encryptCheckBox.setSelected(false);
			} catch (Exception ex) {
				// Display error message if an exception occurs
				showMessage(messageLabel, "Error adding article: " + ex.getMessage());
			}
		});

		// Add input fields and the button to the VBox
		addField(vbox, "Title:", titleField);
		addField(vbox, "Authors (comma-separated):", authorsField);
		addField(vbox, "Abstract:", abstractField);
		addField(vbox, "Keywords (comma-separated):", keywordsField);
		addField(vbox, "Body:", bodyField);
		addField(vbox, "References (comma-separated):", referencesField);
		vbox.getChildren().addAll(encryptCheckBox, addButton, messageLabel);

		return vbox; // Return the completed VBox
	}

	// Creates the "List Articles" tab UI and its functionality.
	public static VBox createListArticlesTab() {
		VBox vbox = createVBox(); // Standard VBox layout
		ListView<String> articlesListView = new ListView<>(); // ListView to display article titles
		Label messageLabel = new Label(); // Label to display messages
		Button refreshButton = new Button("Refresh List"); // Button to refresh the article list

		// Define the behavior when the "Refresh List" button is clicked
		refreshButton.setOnAction(e -> {
			try {
				// Populate the ListView with article titles from the database
				articlesListView.getItems().setAll(databaseHelper.listArticles());
			} catch (Exception ex) {
				// Display error message if an exception occurs
				showMessage(messageLabel, "Error listing articles: " + ex.getMessage());
			}
		});

		// Add components to the VBox
		vbox.getChildren().addAll(refreshButton, articlesListView, messageLabel);

		return vbox; // Return the completed VBox
	}

	// Creates the "View Article" tab UI and its functionality.
	public static VBox createViewArticleTab() {
		VBox vbox = createVBox(); // Standard VBox layout
		TextField articleIdField = new TextField(); // Input field for article ID
		TextArea articleDetailsArea = new TextArea(); // TextArea to display article details
		articleDetailsArea.setEditable(false); // Make the TextArea read-only
		Label messageLabel = new Label(); // Label to display messages
		Button viewButton = new Button("View Article"); // Button to trigger article viewing

		// Define the behavior when the "View Article" button is clicked
		viewButton.setOnAction(e -> {
			try {
				// Parse the article ID and fetch details from the database
				int articleId = Integer.parseInt(articleIdField.getText().trim());
				articleDetailsArea.setText(databaseHelper.viewArticle(articleId));
			} catch (Exception ex) {
				// Display error message if an exception occurs
				showMessage(messageLabel, "Error viewing article: " + ex.getMessage());
			}
		});

		// Add components to the VBox
		addField(vbox, "Article ID:", articleIdField);
		vbox.getChildren().addAll(viewButton, articleDetailsArea, messageLabel);

		return vbox; // Return the completed VBox
	}

	// Creates the "Delete Article" tab UI and its functionality.
	public static VBox createDeleteArticleTab() {
		VBox vbox = createVBox(); // Standard VBox layout
		TextField articleIdField = new TextField(); // Input field for article ID
		Label messageLabel = new Label(); // Label to display messages
		Button deleteButton = new Button("Delete Article"); // Button to trigger article deletion

		// Define the behavior when the "Delete Article" button is clicked
		deleteButton.setOnAction(e -> {
			try {
				// Parse the article ID and delete it from the database
				databaseHelper.deleteArticle(Integer.parseInt(articleIdField.getText().trim()));
				showMessage(messageLabel, "Article deleted successfully!");
			} catch (Exception ex) {
				// Display error message if an exception occurs
				showMessage(messageLabel, "Error deleting article: " + ex.getMessage());
			}
		});

		// Add components to the VBox
		addField(vbox, "Article ID:", articleIdField);
		vbox.getChildren().addAll(deleteButton, messageLabel);

		return vbox; // Return the completed VBox
	}

	public static VBox createBackupArticlesTab() {
		// Create a VBox layout for the "Backup Articles" tab
		VBox vbox = createVBox();

		// Input field for the backup file name
		TextField backupFileField = new TextField();

		// Label to display success or error messages
		Label messageLabel = new Label();

		// Button to trigger the backup operation
		Button backupButton = new Button("Backup Articles");

		// Define action to perform when the "Backup Articles" button is clicked
		backupButton.setOnAction(e -> {
			try {
				// Call the database helper to perform the backup using the provided file name
				databaseHelper.backupArticles(backupFileField.getText().trim());
				// Display a success message
				showMessage(messageLabel, "Backup completed successfully!");
			} catch (Exception ex) {
				// Display an error message if the backup operation fails
				showMessage(messageLabel, "Error backing up articles: " + ex.getMessage());
			}
		});

		// Add the input field and button to the layout
		addField(vbox, "Backup File Name (e.g., backup.txt):", backupFileField);
		vbox.getChildren().addAll(backupButton, messageLabel);
		return vbox;
	}

	public static VBox createRestoreArticlesTab() {
		// Create a VBox layout for the "Restore Articles" tab
		VBox vbox = createVBox();

		// Input field for the restore file name
		TextField restoreFileField = new TextField();

		// Label to display success or error messages
		Label messageLabel = new Label();

		// Button to trigger the restore operation
		Button restoreButton = new Button("Restore Articles");

		// Define action to perform when the "Restore Articles" button is clicked
		restoreButton.setOnAction(e -> {
			try {
				// Call the database helper to restore articles from the provided file name
				databaseHelper.restoreArticles(restoreFileField.getText().trim());
				// Display a success message
				showMessage(messageLabel, "Restore completed successfully!");
			} catch (Exception ex) {
				// Display an error message if the restore operation fails
				showMessage(messageLabel, "Error restoring articles: " + ex.getMessage());
			}
		});

		// Add the input field and button to the layout
		addField(vbox, "Restore File Name (e.g., backup.txt):", restoreFileField);
		vbox.getChildren().addAll(restoreButton, messageLabel);
		return vbox;
	}

	public static VBox createInviteUserTab() {
		// Create a VBox layout for the "Invite User" tab
		VBox vbox = createVBox();

		// Checkboxes to select user roles
		CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor");

		// Labels to display the generated invitation code and messages
		Label codeLabel = new Label(), messageLabel = new Label();

		// Button to generate an invitation code
		Button generateButton = new Button("Generate Invitation Code");

		// Define action to perform when the "Generate Invitation Code" button is
		// clicked
		generateButton.setOnAction(e -> {
			// List to store selected roles
			List<String> roles = new ArrayList<>();

			// Add roles based on checkbox selections
			if (studentCheckBox.isSelected())
				roles.add("Student");
			if (instructorCheckBox.isSelected())
				roles.add("Instructor");

			// Generate an invitation code if at least one role is selected
			if (!roles.isEmpty()) {
				String code = UUID.randomUUID().toString().replace("-", "").substring(0, 4);
				DataStore.getInstance().getInvitations().put(code, new User.Invitation(roles));
				codeLabel.setText("Invitation Code: " + code);
			} else {
				// Display an error message if no roles are selected
				showMessage(messageLabel, "Select at least one role.");
			}
		});

		// Add the components to the layout
		vbox.getChildren().addAll(new Label("Select Roles for Invitation:"), studentCheckBox, instructorCheckBox,
				generateButton, codeLabel, messageLabel);
		return vbox;
	}

	public static VBox createResetUserTab() {
		// Create a VBox layout for the "Reset User Account" tab
		VBox vbox = createVBox();

		// Input fields for username and expiry time
		TextField usernameField = new TextField(), expiryTimeField = new TextField();

		// Date picker for selecting expiry date
		DatePicker expiryDatePicker = new DatePicker();

		// Label to display success or error messages
		Label messageLabel = new Label();

		// Button to reset the user account
		Button resetButton = new Button("Reset User Account");

		// Define action to perform when the "Reset User Account" button is clicked
		resetButton.setOnAction(e -> {
			// Find the user by username
			User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());

			if (user != null) {
				// Generate a one-time password (OTP)
				String otp = UUID.randomUUID().toString().substring(0, 4);

				// Parse the expiry date and time
				LocalDateTime expiry = expiryDatePicker.getValue().atTime(
						Integer.parseInt(expiryTimeField.getText().split(":")[0]),
						Integer.parseInt(expiryTimeField.getText().split(":")[1]));

				// Set the OTP and expiry for the user
				user.setOneTimePassword(otp, expiry);

				// Display the OTP in a success message
				showMessage(messageLabel, "One-time password set: " + otp);
			} else {
				// Display an error message if the user is not found
				showMessage(messageLabel, "User not found.");
			}
		});

		// Add the components to the layout
		vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Expiry Date:"), expiryDatePicker,
				new Label("Expiry Time (HH:MM):"), expiryTimeField, resetButton, messageLabel);
		return vbox;
	}

	public static VBox createDeleteUserTab() {
		// Create a VBox layout for the "Delete User" tab
		VBox vbox = createVBox();

		// Input field for username
		TextField usernameField = new TextField();

		// Label to display messages
		Label messageLabel = new Label();

		// Button to trigger the delete user operation
		Button deleteButton = new Button("Delete User Account");

		// Define action to perform when the "Delete User Account" button is clicked
		deleteButton.setOnAction(e -> {
			// Find the user by username
			User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());

			if (user != null) {
				// Show confirmation dialog before deleting the user
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
				alert.showAndWait().ifPresent(response -> {
					if (response == ButtonType.YES) {
						// Remove the user from the user list
						DataStore.getInstance().getUserList().remove(user);
						showMessage(messageLabel, "User account deleted.");
					}
				});
			} else {
				// Display an error message if the user is not found
				showMessage(messageLabel, "User not found.");
			}
		});

		// Add the components to the layout
		vbox.getChildren().addAll(new Label("Username:"), usernameField, deleteButton, messageLabel);
		return vbox;
	}

	public static VBox createListUsersTab() {
		// Create a VBox layout for the "List Users" tab
		VBox vbox = createVBox();

		// ListView to display user information
		ListView<String> userListView = new ListView<>();

		// Button to refresh the list of users
		Button refreshButton = new Button("Refresh List");

		// Define action to perform when the "Refresh List" button is clicked
		refreshButton.setOnAction(e -> {
			// Clear the current items in the ListView
			userListView.getItems().clear();

			// Populate the ListView with user details
			DataStore.getInstance().getUserList().forEach(user -> {
				userListView.getItems().add("Username: " + user.getUsername() + ", Name: " + user.getFullName()
						+ ", Roles: " + String.join(", ", user.getRoles()));
			});
		});

		// Add the components to the layout
		vbox.getChildren().addAll(refreshButton, userListView);
		return vbox;
	}

	public static VBox createManageRolesTab() {
		// Create a VBox layout for the "Manage Roles" tab
		VBox vbox = createVBox();

		// Input field for username
		TextField usernameField = new TextField();

		// Checkboxes to assign roles to the user
		CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor"),
				adminCheckBox = new CheckBox("Administrator");

		// Label to display messages
		Label messageLabel = new Label();

		// Button to update roles for the user
		Button updateButton = new Button("Update Roles");

		// Define action to perform when the "Update Roles" button is clicked
		updateButton.setOnAction(e -> {
			// Find the user by username
			User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());

			if (user != null) {
				// Create a list to store selected roles
				List<String> roles = new ArrayList<>();

				// Add roles based on checkbox selections
				if (studentCheckBox.isSelected())
					roles.add("Student");
				if (instructorCheckBox.isSelected())
					roles.add("Instructor");
				if (adminCheckBox.isSelected())
					roles.add("Administrator");

				if (!roles.isEmpty()) {
					// Update the user's roles
					user.setRoles(roles);
					showMessage(messageLabel, "Roles updated.");
				} else {
					// Display an error message if no roles are selected
					showMessage(messageLabel, "Select at least one role.");
				}
			} else {
				// Display an error message if the user is not found
				showMessage(messageLabel, "User not found.");
			}
		});

		// Add the components to the layout
		vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Assign Roles:"), studentCheckBox,
				instructorCheckBox, adminCheckBox, updateButton, messageLabel);
		return vbox;
	}

	public static VBox createManageGroupsTab() {
		// Create a VBox layout for the "Manage Groups" tab
		VBox vbox = createVBox();

		// Input fields for group name, username, and article ID
		TextField groupNameField = new TextField();
		TextField usernameField = new TextField();
		TextField articleIdField = new TextField();

		// Radio buttons to select group type
		RadioButton specialGroupRadio = new RadioButton("Special Group");
		RadioButton generalGroupRadio = new RadioButton("General Group");
		ToggleGroup groupTypeToggle = new ToggleGroup();
		specialGroupRadio.setToggleGroup(groupTypeToggle);
		generalGroupRadio.setToggleGroup(groupTypeToggle);

		// Radio buttons to select user rights
		RadioButton adminRightsRadio = new RadioButton("Grant Admin Rights");
		RadioButton viewRightsRadio = new RadioButton("Grant View Rights");
		ToggleGroup userRightsToggle = new ToggleGroup();
		adminRightsRadio.setToggleGroup(userRightsToggle);
		viewRightsRadio.setToggleGroup(userRightsToggle);

		// Buttons for group management actions
		Button addToGroupButton = new Button("Add Article/User to Group");
		Button deleteGroupButton = new Button("Delete Group");
		Button removeUserButton = new Button("Remove User from Group");

		// Label to display messages
		Label messageLabel = new Label();

		// Define action to perform when the "Add to Group" button is clicked
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

		// Define action to perform when the "Delete Group" button is clicked
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

		// Define action to perform when the "Remove User from Group" button is clicked
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
		vbox.getChildren().addAll(new Label("Group Name:"), groupNameField, specialGroupRadio, generalGroupRadio,
				new Label("Username:"), usernameField, adminRightsRadio, viewRightsRadio, new Label("Article ID:"),
				articleIdField, addToGroupButton, deleteGroupButton, removeUserButton, messageLabel);

		return vbox;
	}

	public static VBox createViewGroupUsersTab() {
		// Create a VBox layout for the "View Group Users" tab
		VBox vbox = createVBox();

		// Input field for the group name
		TextField groupNameField = new TextField();

		// Label to display messages
		Label messageLabel = new Label();

		// TableView to display users in the group
		TableView<Map<String, String>> userTable = new TableView<>();

		// Button to trigger the action of viewing group users
		Button viewUsersButton = new Button("View Group Users");

		// Define columns for the user table
		String[][] columns = { { "Username", "username" }, { "Role", "role" }, { "Can View", "canView" },
				{ "Can Admin", "canAdmin" } };

		// Add columns to the user table
		for (String[] col : columns) {
			TableColumn<Map<String, String>, String> tableColumn = new TableColumn<>(col[0]);
			tableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(col[1])));
			userTable.getColumns().add(tableColumn);
		}

		// Define action for the "View Group Users" button
		viewUsersButton.setOnAction(e -> {
			try {
				// Get the group name and validate it
				String groupName = groupNameField.getText().trim();
				if (groupName.isEmpty())
					throw new IllegalArgumentException("Group name cannot be empty.");

				// Retrieve the group ID and load users in the group
				String groupId = databaseHelper.getGroupIdByName(groupName);
				userTable.getItems().setAll(databaseHelper.getUsersInGroup(groupId));
				showMessage(messageLabel, "Users loaded.");
			} catch (Exception ex) {
				// Display error message and clear the table if an exception occurs
				showMessage(messageLabel, "Error: " + ex.getMessage());
				userTable.getItems().clear();
			}
		});

		// Add components to the layout
		vbox.getChildren().addAll(new Label("Group Name:"), groupNameField, viewUsersButton, userTable, messageLabel);

		return vbox;
	}

	public static VBox createViewArticlesInGroupTab() {
		// Create a VBox layout for the "View Articles in Group" tab
		VBox vbox = createVBox();

		// Input fields for the group name and username
		TextField groupNameField = new TextField();
		TextField usernameField = new TextField();

		// Label to display messages
		Label messageLabel = new Label();

		// TableView to display articles in the group
		TableView<Map<String, String>> articlesTable = new TableView<>();

		// Button to trigger the action of viewing articles in the group
		Button viewArticlesButton = new Button("View Articles");

		// Define and add columns to the articles table
		TableColumn<Map<String, String>, String> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("id")));

		TableColumn<Map<String, String>, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("title")));

		TableColumn<Map<String, String>, String> bodyColumn = new TableColumn<>("Body");
		bodyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get("body")));

		articlesTable.getColumns().setAll(List.of(idColumn, titleColumn, bodyColumn));

		// Define action for the "View Articles" button
		viewArticlesButton.setOnAction(e -> {
			try {
				// Get the group name and username, and validate them
				String groupName = groupNameField.getText().trim();
				String username = usernameField.getText().trim();

				if (groupName.isEmpty() || username.isEmpty()) {
					showMessage(messageLabel, "Group name and username cannot be empty.");
					articlesTable.getItems().clear();
					return;
				}

				// Retrieve the group ID and load articles in the group
				String groupId = databaseHelper.getGroupIdByName(groupName);
				List<Map<String, String>> articles = databaseHelper.getArticlesInGroup(groupId, username);
				articlesTable.getItems().setAll(articles);
				showMessage(messageLabel, "Articles loaded.");
			} catch (Exception ex) {
				// Display error message and clear the table if an exception occurs
				showMessage(messageLabel, "Error: " + ex.getMessage());
				articlesTable.getItems().clear();
			}
		});

		// Add components to the layout
		vbox.getChildren().addAll(new Label("Group Name:"), groupNameField, new Label("Username:"), usernameField,
				viewArticlesButton, articlesTable, messageLabel);

		return vbox;
	}
}
