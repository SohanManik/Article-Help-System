package controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import model.DataStore;
import model.Invitation;
import model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AdminTabs {

	public static VBox createAddArticleUserTab() {
	        VBox vbox = new VBox(10);
	        vbox.getChildren().addAll(
	        		new Label("Enter article title: "), usernameField,
	        		new Label("Enter author(s) (comma-separated): "), expiryDatePicker,
	        		new Label("Enter article abstract: "), usernameField,
	        		new Label("Enter keywords (comma-separated): "), expiryDatePicker,
	        		new Label("Enter article body: "), usernameField,
	        		new Label("Enter references (comma-separated): "), expiryTimeField, addArticleButton, messageLabel);
	        return vbox;
	    }
	
//    public static VBox createResetUserTab() {
//        VBox vbox = new VBox(10);
//        TextField usernameField = new TextField(), expiryTimeField = new TextField();
//        DatePicker expiryDatePicker = new DatePicker();
//        Label messageLabel = new Label();
//        Button resetButton = new Button("Reset User Account");
//        resetButton.setOnAction(e -> {
//            User user = DataStore.getInstance().findUserByUsername(usernameField.getText().trim());
//            if (user != null) {
//                String oneTimePassword = UUID.randomUUID().toString().substring(0, 4);
//                LocalDateTime expiryDateTime = expiryDatePicker.getValue().atTime(
//                        Integer.parseInt(expiryTimeField.getText().split(":")[0]),
//                        Integer.parseInt(expiryTimeField.getText().split(":")[1]));
//                user.setOneTimePassword(oneTimePassword, expiryDateTime);
//                messageLabel.setText("One-time password set: " + oneTimePassword);
//            } else messageLabel.setText("User not found.");
//        });
//        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Expiry Date:"),
//                expiryDatePicker, new Label("Expiry Time (HH:MM):"), expiryTimeField, resetButton, messageLabel);
//        return vbox;
//    }

	
	public static VBox createInviteUserTab() {
        VBox vbox = new VBox(10);
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
            } else messageLabel.setText("Select at least one role.");
        });
        vbox.getChildren().addAll(new Label("Select Roles for Invitation:"), studentCheckBox, instructorCheckBox,
                generateCodeButton, codeLabel, messageLabel);
        return vbox;
    }

    public static VBox createResetUserTab() {
        VBox vbox = new VBox(10);
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
                messageLabel.setText("One-time password set: " + oneTimePassword);
            } else messageLabel.setText("User not found.");
        });
        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Expiry Date:"),
                expiryDatePicker, new Label("Expiry Time (HH:MM):"), expiryTimeField, resetButton, messageLabel);
        return vbox;
    }

    public static VBox createDeleteUserTab() {
        VBox vbox = new VBox(10);
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
                        messageLabel.setText("User account deleted.");
                    }
                });
            } else messageLabel.setText("User not found.");
        });
        vbox.getChildren().addAll(new Label("Username:"), usernameField, deleteButton, messageLabel);
        return vbox;
    }

    public static VBox createListUsersTab() {
        VBox vbox = new VBox(10);
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
        TextField usernameField = new TextField();
        CheckBox studentCheckBox = new CheckBox("Student"), instructorCheckBox = new CheckBox("Instructor"),
                adminCheckBox = new CheckBox("Administrator");
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
                    messageLabel.setText("Roles updated.");
                } else messageLabel.setText("Select at least one role.");
            } else messageLabel.setText("User not found.");
        });
        vbox.getChildren().addAll(new Label("Username:"), usernameField, new Label("Assign Roles:"),
                studentCheckBox, instructorCheckBox, adminCheckBox, updateRolesButton, messageLabel);
        return vbox;
    }
}
