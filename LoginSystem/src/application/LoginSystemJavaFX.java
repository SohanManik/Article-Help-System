package application;

import controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;

public class LoginSystemJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login System");
        LoginController loginController = new LoginController(primaryStage);
        loginController.showLoginPage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
