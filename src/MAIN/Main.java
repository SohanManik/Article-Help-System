package MAIN;

import Controller.Auth_Control;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // Entry point of the JavaFX application
    @Override
    public void start(Stage primaryStage) {
        // Set the title of the primary stage
        primaryStage.setTitle("Login System");

        // Show the login page using the Auth_Control class
        new Auth_Control(primaryStage).showLoginPage();
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
