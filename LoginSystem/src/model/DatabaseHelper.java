package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    
    // Singleton instance and database connection reference
    private static DatabaseHelper instance;
    private Connection connection;

    // Private constructor to establish a database connection and set up the database
    private DatabaseHelper() throws SQLException {
        String url = "jdbc:h2:~/test";	// Change path as needed
        String user = "sa";
        String password = "";			// Default H2 password is empty
        connection = DriverManager.getConnection(url, user, password);
        setupDatabase();
    }

    // Singleton pattern to get a single instance of DatabaseHelper
    public static DatabaseHelper getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    // Sets up the database by creating the Articles table if it does not exist
    private void setupDatabase() throws SQLException {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Articles (
                id INT PRIMARY KEY AUTO_INCREMENT,
                title VARCHAR(255),
                authors VARCHAR(255),
                abstractText TEXT,
                keywords VARCHAR(255),
                body TEXT,
                references TEXT
            );
        """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // Adds a new article record to the Articles table
    public void addArticle(String title, String authors, String abstractText, String keywords, String body, String references) throws SQLException {
        String sql = "INSERT INTO Articles (title, authors, abstractText, keywords, body, references) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, authors);
            pstmt.setString(3, abstractText);
            pstmt.setString(4, keywords);
            pstmt.setString(5, body);
            pstmt.setString(6, references);
            pstmt.executeUpdate();
        }
    }

    
    // Retrieves a list of articles with their IDs, titles, and authors
    public List<String> listArticles() throws SQLException {
        String sql = "SELECT id, title, authors FROM Articles ORDER BY id";
        List<String> articles = new ArrayList<>();
        int displayId = 1; // Start display ID at 1
        
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                articles.add("ID: " + displayId + ", Title: " + title + ", Authors: " + authors);
                displayId++; // Increment display ID
            }
        }
        return articles;
    }


 // Helper method to map display ID to actual database ID
    private int getDatabaseIdForDisplayId(int displayId) throws SQLException {
        String sql = "SELECT id FROM Articles ORDER BY id";
        int currentDisplayId = 1; // Display ID starts at 1

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int dbId = rs.getInt("id");
                if (currentDisplayId == displayId) {
                    return dbId; // Return the actual database ID for the given display ID
                }
                currentDisplayId++;
            }
        }
        throw new SQLException("Invalid display ID: " + displayId);
    }

    
    // Retrieves full details of a specific article based on its ID
    public String viewArticle(int displayId) throws SQLException {
        int articleId = getDatabaseIdForDisplayId(displayId); // Get actual DB ID from display ID
        String sql = "SELECT * FROM Articles WHERE id = ?";
        StringBuilder articleDetails = new StringBuilder();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    articleDetails.append("ID: ").append(displayId) // Show display ID, not DB ID
                            .append("\nTitle: ").append(rs.getString("title"))
                            .append("\nAuthors: ").append(rs.getString("authors"))
                            .append("\nAbstract: ").append(rs.getString("abstractText"))
                            .append("\nKeywords: ").append(rs.getString("keywords"))
                            .append("\nBody: ").append(rs.getString("body"))
                            .append("\nReferences: ").append(rs.getString("references"));
                } else {
                    return "Article not found.";
                }
            }
        }
        return articleDetails.toString();
    }

    // Deletes an article from the Articles table based on its ID
    public void deleteArticle(int displayId) throws SQLException {
        int articleId = getDatabaseIdForDisplayId(displayId); // Get actual DB ID from display ID
        String sql = "DELETE FROM Articles WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, articleId);
            pstmt.executeUpdate();
        }
    }

    // Deletes an article from the Articles table based on its ID
    public void backupArticles(String backupFileName) throws SQLException {
        String backupSQL = String.format("SCRIPT TO '%s'", backupFileName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(backupSQL);
        }
    }

    // Restores the database from a specified backup file
    public void restoreArticles(String backupFileName) throws SQLException {
        // Drop existing table if it exists to avoid conflicts
        String dropTableSQL = "DROP TABLE IF EXISTS Articles";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(dropTableSQL);
        }

        // Run restore script
        String restoreSQL = String.format("RUNSCRIPT FROM '%s'", backupFileName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(restoreSQL);
        }
    }
}
