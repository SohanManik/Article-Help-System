package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LoginSystemJavaFX extends Application {

    private static List<User> userList = new ArrayList<>();

    private TextField usernameField, emailField, firstNameField, middleNameField, lastNameField, preferredFirstNameField;
    private PasswordField passwordField, confirmPasswordField;
    private Label messageLabel, setupMessageLabel;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login System");
        showLoginPage();
    }

    private void showLoginPage() {
        GridPane grid = createGridPane();

        usernameField = new TextField();
        passwordField = new PasswordField();
        confirmPasswordField = new PasswordField();
        messageLabel = new Label();

        addGridComponents(grid, new Label("Username:"), usernameField, 0);
        addGridComponents(grid, new Label("Password:"), passwordField, 1);
        addGridComponents(grid, new Label("Confirm Password:"), confirmPasswordField, 2);

        Button loginButton = new Button("Login / Register");
        loginButton.setOnAction(e -> handleLoginOrRegister());
        grid.add(loginButton, 1, 3);

        grid.add(messageLabel, 1, 4);

        setScene(grid, 400, 250);
    }

    private void handleLoginOrRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (userList.isEmpty() && validatePassword(password, confirmPassword)) {
            registerAdmin(username, password);
        } else {
            User user = findUserByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                if (!user.isAccountSetupComplete()) {
                    showAccountSetupPage(user);
                } else {
                    proceedAfterLogin(user);
                }
            } else if (user == null && validatePassword(password, confirmPassword)) {
                showRoleSelectionForRegistration(username, password);
            } else {
                messageLabel.setText("Invalid login or registration details.");
            }
        }
        clearFields();
    }

    private boolean validatePassword(String password, String confirmPassword) {
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("Password fields cannot be empty.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return false;
        }
        return true;
    }

    private void registerAdmin(String username, String password) {
        User admin = new User(username, password);
        admin.addRole("Administrator");
        userList.add(admin);
        messageLabel.setText("Admin account created. Please log in again.");
    }

    private User findUserByUsername(String username) {
        return userList.stream().filter(user -> user.getUsername().equals(username)).findFirst().orElse(null);
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void showAccountSetupPage(User user) {
        GridPane grid = createGridPane();

        emailField = new TextField();
        firstNameField = new TextField();
        middleNameField = new TextField();
        lastNameField = new TextField();
        preferredFirstNameField = new TextField();
        setupMessageLabel = new Label();

        addGridComponents(grid, new Label("Email:"), emailField, 0);
        addGridComponents(grid, new Label("First Name:"), firstNameField, 1);
        addGridComponents(grid, new Label("Middle Name:"), middleNameField, 2);
        addGridComponents(grid, new Label("Last Name:"), lastNameField, 3);
        addGridComponents(grid, new Label("Preferred First Name:"), preferredFirstNameField, 4);

        Button finishSetupButton = new Button("Finish Setup");
        finishSetupButton.setOnAction(e -> handleFinishSetup(user));
        grid.add(finishSetupButton, 1, 5);
        grid.add(setupMessageLabel, 1, 6);

        setScene(grid, 400, 350);
    }

    private void handleFinishSetup(User user) {
        if (fieldsAreEmpty(emailField, firstNameField, lastNameField)) {
            setupMessageLabel.setText("Please fill in all required fields.");
        } else {
            user.setDetails(emailField.getText(), firstNameField.getText(), middleNameField.getText(),
                            lastNameField.getText(), preferredFirstNameField.getText());
            user.setAccountSetupComplete(true);
            proceedAfterLogin(user);
        }
    }

    private void proceedAfterLogin(User user) {
        if (user.getRoles().size() > 1) {
            showRoleSelectionPage(user);
        } else {
            showHomePage(user, user.getRoles().get(0));
        }
    }

    private void showRoleSelectionPage(User user) {
        VBox vbox = createVBox();

        ToggleGroup roleGroup = new ToggleGroup();
        user.getRoles().forEach(role -> {
            RadioButton roleButton = new RadioButton(role);
            roleButton.setToggleGroup(roleGroup);
            vbox.getChildren().add(roleButton);
        });

        Button proceedButton = new Button("Proceed");
        proceedButton.setOnAction(e -> {
            RadioButton selectedRoleButton = (RadioButton) roleGroup.getSelectedToggle();
            if (selectedRoleButton != null) {
                showHomePage(user, selectedRoleButton.getText());
            }
        });

        vbox.getChildren().add(proceedButton);
        setScene(vbox, 300, 200);
    }

    private void showHomePage(User user, String role) {
        VBox vbox = createVBox();
        String displayName = user.getPreferredFirstNameOrDefault();
        Label welcomeLabel = new Label("Welcome, " + role + " " + displayName + "!");
        vbox.getChildren().add(welcomeLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> showLoginPage());
        vbox.getChildren().add(logoutButton);

        if ("Administrator".equals(role)) {
            vbox.getChildren().add(new Label("Administrator functionalities go here."));
        }

        setScene(vbox, 400, 300);
    }

    private void showRoleSelectionForRegistration(String username, String password) {
        VBox vbox = createVBox();

        CheckBox studentCheckBox = new CheckBox("Student");
        CheckBox instructorCheckBox = new CheckBox("Instructor");
        vbox.getChildren().addAll(new Label("Select your roles:"), studentCheckBox, instructorCheckBox);

        Button registerButton = new Button("Register");
        registerButton.setOnAction(e -> {
            List<String> roles = new ArrayList<>();
            if (studentCheckBox.isSelected()) roles.add("Student");
            if (instructorCheckBox.isSelected()) roles.add("Instructor");

            if (!roles.isEmpty()) {
                User newUser = new User(username, password, roles);
                userList.add(newUser);
                messageLabel.setText("Registration successful. Please log in.");
                showLoginPage();
            } else {
                messageLabel.setText("Please select at least one role.");
            }
        });

        vbox.getChildren().add(registerButton);
        setScene(vbox, 300, 200);
    }

    private void setScene(javafx.scene.Parent root, int width, int height) {
        primaryStage.setScene(new Scene(root, width, height));
        primaryStage.show();
    }

    private GridPane createGridPane() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setVgap(8);
        grid.setHgap(10);
        return grid;
    }

    private VBox createVBox() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private void addGridComponents(GridPane grid, Label label, TextField field, int row) {
        grid.add(label, 0, row);
        grid.add(field, 1, row);
    }

    private boolean fieldsAreEmpty(TextField... fields) {
        for (TextField field : fields) {
            if (field.getText().trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class User {
        private String username, password, email, firstName, middleName, lastName, preferredFirstName;
        private List<String> roles;
        private boolean accountSetupComplete = false;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.roles = new ArrayList<>();
        }

        public User(String username, String password, List<String> roles) {
            this(username, password);
            this.roles.addAll(roles);
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public List<String> getRoles() { return roles; }
        public boolean isAccountSetupComplete() { return accountSetupComplete; }

        public void setDetails(String email, String firstName, String middleName, String lastName, String preferredFirstName) {
            this.email = email;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.preferredFirstName = preferredFirstName;
        }

        public void setAccountSetupComplete(boolean complete) {
            this.accountSetupComplete = complete;
        }

        public String getPreferredFirstNameOrDefault() {
            return preferredFirstName == null || preferredFirstName.isEmpty() ? firstName : preferredFirstName;
        }

        public void addRole(String role) {
            if (!roles.contains(role)) roles.add(role);
        }
    }
}
