package Controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.List;
import model.DataStore;
import model.DatabaseHelper;

public class StudentTabs {

    private static DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    private static VBox createVBox() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        return vbox;
    }

    private static void addField(VBox vbox, String labelText, Control field) {
        vbox.getChildren().addAll(new Label(labelText), field);
    }

    public static VBox createHelpSystemTab() {
        VBox vbox = createVBox();

        TextField genericMessageField = new TextField();
        Button sendGenericButton = new Button("Send Generic Message");
        Label genericMessageLabel = new Label();

        sendGenericButton.setOnAction(e -> {
            String message = genericMessageField.getText().trim();
            if (!message.isEmpty()) {
            	DataStore.sendGenericMessage(message);
                genericMessageLabel.setText("Generic message sent.");
                genericMessageField.clear();
            } else {
                genericMessageLabel.setText("Please enter a message.");
            }
        });

        TextField queryField = new TextField();
        TextArea specificMessageField = new TextArea();
        Button sendSpecificButton = new Button("Send Specific Message");
        Label specificMessageLabel = new Label();

        sendSpecificButton.setOnAction(e -> {
            String query = queryField.getText().trim();
            String message = specificMessageField.getText().trim();

            if (!query.isEmpty() && !message.isEmpty()) {
            	DataStore.sendSpecificMessage(query, message);
                specificMessageLabel.setText("Specific message sent for query: " + query);
                queryField.clear();
                specificMessageField.clear();
            } else {
                specificMessageLabel.setText("Please enter a query and message.");
            }
        });

        addField(vbox, "Generic Message:", genericMessageField);
        vbox.getChildren().addAll(sendGenericButton, genericMessageLabel);
        addField(vbox, "Specific Query:", queryField);
        addField(vbox, "Specific Message:", specificMessageField);
        vbox.getChildren().addAll(sendSpecificButton, specificMessageLabel);

        return vbox;
    }

    public static VBox createSearchArticlesTab() {
        VBox vbox = createVBox();

        TextField searchField = new TextField();
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>();
        ChoiceBox<String> groupChoiceBox = new ChoiceBox<>();
        ListView<String> resultsListView = new ListView<>();
        Label messageLabel = new Label();
        Button searchButton = new Button("Search");

        levelChoiceBox.getItems().addAll("All", "Beginner", "Intermediate", "Advanced", "Expert");
        levelChoiceBox.setValue("All");

        groupChoiceBox.getItems().addAll("All", "Assignment 1", "Assignment 2");
        groupChoiceBox.setValue("All");

        searchButton.setOnAction(e -> {
            try {
                String searchText = searchField.getText().trim();
                String level = levelChoiceBox.getValue();
                String group = groupChoiceBox.getValue();

                List<String> results = databaseHelper.searchArticles(searchText, level, group);
                resultsListView.getItems().setAll(results);

                String activeGroup = "Active Group: " + group;
                String levelStats = databaseHelper.getLevelStatistics(results);
                messageLabel.setText(activeGroup + "\n" + levelStats);
            } catch (Exception ex) {
                messageLabel.setText("Error during search: " + ex.getMessage());
            }
        });

        addField(vbox, "Search Text:", searchField);
        vbox.getChildren().addAll(new Label("Content Level:"), levelChoiceBox);
        vbox.getChildren().addAll(new Label("Group:"), groupChoiceBox);
        vbox.getChildren().addAll(searchButton, resultsListView, messageLabel);

        return vbox;
    }
}