package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper {
    
    private static DatabaseHelper instance;
    private Connection connection;

    private DatabaseHelper() throws SQLException {
        String url = "jdbc:h2:~/test"; // Change path as needed
        String user = "sa";
        String password = ""; // Default H2 password is empty
        connection = DriverManager.getConnection(url, user, password);
        setupDatabase();
    }

    public static DatabaseHelper getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

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

    public List<String> listArticles() throws SQLException {
        String sql = "SELECT id, title, authors FROM Articles";
        List<String> articles = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                articles.add("ID: " + id + ", Title: " + title + ", Authors: " + authors);
            }
        }
        return articles;
    }

    public String viewArticle(int articleId) throws SQLException {
        String sql = "SELECT * FROM Articles WHERE id = ?";
        StringBuilder articleDetails = new StringBuilder();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    articleDetails.append("ID: ").append(rs.getInt("id"))
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

    public void deleteArticle(int articleId) throws SQLException {
        String sql = "DELETE FROM Articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, articleId);
            pstmt.executeUpdate();
        }
    }

    public void backupArticles(String backupFileName) throws SQLException {
        String backupSQL = String.format("SCRIPT TO '%s'", backupFileName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(backupSQL);
        }
    }

    public void restoreArticles(String backupFileName) throws SQLException {
        String restoreSQL = String.format("RUNSCRIPT FROM '%s'", backupFileName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(restoreSQL);
        }
    }
}
