package Controller;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import java.util.List;
import model.DataStore;
import model.DatabaseHelper;

public class StudentTabs {

    // Singleton instance of the DatabaseHelper for database interactions
    private static DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    // Utility method to create a VBox with consistent spacing and padding
    private static VBox createVBox() {
        VBox vbox = new VBox(10); // 10px spacing between elements
        vbox.setPadding(new Insets(10)); // 10px padding around the VBox
        return vbox;
    }

    // Utility method to add a label and a control (e.g., text field) to a VBox
    private static void addField(VBox vbox, String labelText, Control field) {
        vbox.getChildren().addAll(new Label(labelText), field);
    }

    // Creates the "Help System" tab for students
    public static VBox createHelpSystemTab() {
        VBox vbox = createVBox(); // Standard layout

        // Generic message components
        TextField genericMessageField = new TextField(); // Input field for generic messages
        Button sendGenericButton = new Button("Send Generic Message"); // Button to send a generic message
        Label genericMessageLabel = new Label(); // Label to display the status of the operation

        // Action for sending a generic message
        sendGenericButton.setOnAction(e -> {
            String message = genericMessageField.getText().trim(); // Get the input text
            if (!message.isEmpty()) {
                DataStore.sendGenericMessage(message); // Send the message via the DataStore
                genericMessageLabel.setText("Generic message sent."); // Display success message
                genericMessageField.clear(); // Clear the input field
            } else {
                genericMessageLabel.setText("Please enter a message."); // Prompt the user to enter a message
            }
        });

        // Specific query and message components
        TextField queryField = new TextField(); // Input field for specific queries
        TextArea specificMessageField = new TextArea(); // Text area for detailed specific messages
        Button sendSpecificButton = new Button("Send Specific Message"); // Button to send a specific message
        Label specificMessageLabel = new Label(); // Label to display the status of the operation

        // Action for sending a specific message
        sendSpecificButton.setOnAction(e -> {
            String query = queryField.getText().trim(); // Get the query text
            String message = specificMessageField.getText().trim(); // Get the message text

            if (!query.isEmpty() && !message.isEmpty()) {
                DataStore.sendSpecificMessage(query, message); // Send the specific message via the DataStore
                specificMessageLabel.setText("Specific message sent for query: " + query); // Display success message
                queryField.clear(); // Clear the query field
                specificMessageField.clear(); // Clear the message field
            } else {
                specificMessageLabel.setText("Please enter a query and message."); // Prompt the user to complete both fields
            }
        });

        // Add components for generic message functionality to the layout
        addField(vbox, "Generic Message:", genericMessageField);
        vbox.getChildren().addAll(sendGenericButton, genericMessageLabel);

        // Add components for specific query functionality to the layout
        addField(vbox, "Specific Query:", queryField);
        addField(vbox, "Specific Message:", specificMessageField);
        vbox.getChildren().addAll(sendSpecificButton, specificMessageLabel);

        return vbox; // Return the completed layout
    }

    // Creates the "Search Articles" tab for students
    public static VBox createSearchArticlesTab() {
        VBox vbox = createVBox(); // Standard layout

        // Components for searching articles
        TextField searchField = new TextField(); // Input field for search text
        ChoiceBox<String> levelChoiceBox = new ChoiceBox<>(); // Dropdown for content levels
        ChoiceBox<String> groupChoiceBox = new ChoiceBox<>(); // Dropdown for groups
        ListView<String> resultsListView = new ListView<>(); // List to display search results
        Label messageLabel = new Label(); // Label to display messages or stats
        Button searchButton = new Button("Search"); // Button to trigger the search

        // Populate level choice box with options
        levelChoiceBox.getItems().addAll("All", "Beginner", "Intermediate", "Advanced", "Expert");
        levelChoiceBox.setValue("All"); // Default value

        // Populate group choice box with options
        groupChoiceBox.getItems().addAll("All", "Assignment 1", "Assignment 2");
        groupChoiceBox.setValue("All"); // Default value

        // Action for the "Search" button
        searchButton.setOnAction(e -> {
            try {
                String searchText = searchField.getText().trim(); // Get the search text
                String level = levelChoiceBox.getValue(); // Get the selected level
                String group = groupChoiceBox.getValue(); // Get the selected group

                // Perform the search using the database helper
                List<String> results = databaseHelper.searchArticles(searchText, level, group);
                resultsListView.getItems().setAll(results); // Display the results

                // Get and display group and level statistics
                String activeGroup = "Active Group: " + group;
                String levelStats = databaseHelper.getLevelStatistics(results);
                messageLabel.setText(activeGroup + "\n" + levelStats);
            } catch (Exception ex) {
                messageLabel.setText("Error during search: " + ex.getMessage()); // Handle errors
            }
        });

        // Add components for search functionality to the layout
        addField(vbox, "Search Text:", searchField);
        vbox.getChildren().addAll(new Label("Content Level:"), levelChoiceBox);
        vbox.getChildren().addAll(new Label("Group:"), groupChoiceBox);
        vbox.getChildren().addAll(searchButton, resultsListView, messageLabel);

        return vbox; // Return the completed layout
    }
}