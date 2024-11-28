package MAIN;

import Controller.Auth_Control;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login System");
        new Auth_Control(primaryStage).showLoginPage();
    }

    public static void main(String[] args) {
        launch(args);
    }
}