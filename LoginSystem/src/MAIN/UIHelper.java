package application;

// Importing the LoginController class, which manages the login functionality
import controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

// Main class for the JavaFX application, extending Application to set up the GUI
public class LoginSystemJavaFX extends Application {

    // Overriding the start method, the entry point for JavaFX applications
    @Override
    public void start(Stage primaryStage) {
        // Setting the window title for the login system
        primaryStage.setTitle("Login System");
        
        // Creating an instance of LoginController with the primary stage passed in
        LoginController loginController = new LoginController(primaryStage);
        
        // Displaying the login page using the login controller
        loginController.showLoginPage();
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        // Launching the application with any command-line arguments
        launch(args);
    }
}
