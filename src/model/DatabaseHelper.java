package model;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseHelper {

    // Singleton instance of DatabaseHelper
    private static DatabaseHelper instance;

    // Connection object for interacting with the database
    private Connection connection;

    // Private constructor to set up the database connection and initialize the schema
    private DatabaseHelper() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", ""); // Connect to H2 database
        setupDatabase(); // Initialize the database schema
    }

    // Returns the singleton instance of DatabaseHelper
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            try {
                instance = new DatabaseHelper();
            } catch (SQLException e) {
                e.printStackTrace(); // Print the stack trace in case of an error
            }
        }
        return instance;
    }

    // Sets up the database schema by creating necessary tables
    private void setupDatabase() throws SQLException {
        // SQL statement to create the Articles table
        String createArticlesTable = """
            CREATE TABLE IF NOT EXISTS Articles (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                authors VARCHAR(255),
                abstractText TEXT,
                keywords VARCHAR(255),
                body TEXT,
                references TEXT,
                isEncrypted BOOLEAN DEFAULT FALSE
            );
        """;

        // SQL statement to create the SpecialAccessGroups table
        String createGroupsTable = """
            CREATE TABLE IF NOT EXISTS SpecialAccessGroups (
                groupId VARCHAR(255) PRIMARY KEY,
                groupName VARCHAR(255) UNIQUE NOT NULL,
                groupType VARCHAR(50) NOT NULL
            );
        """;

        // SQL statement to create the GroupUsers table
        String createGroupUsersTable = """
            CREATE TABLE IF NOT EXISTS GroupUsers (
                groupId VARCHAR(255),
                username VARCHAR(255),
                role VARCHAR(50),
                canView BOOLEAN DEFAULT FALSE,
                canAdmin BOOLEAN DEFAULT FALSE,
                PRIMARY KEY (groupId, username),
                FOREIGN KEY (groupId) REFERENCES SpecialAccessGroups(groupId) ON DELETE CASCADE
            );
        """;

        // SQL statement to create the GroupArticles table
        String createGroupArticlesTable = """
            CREATE TABLE IF NOT EXISTS GroupArticles (
                groupId VARCHAR(255),
                articleId INT,
                PRIMARY KEY (groupId, articleId),
                FOREIGN KEY (groupId) REFERENCES SpecialAccessGroups(groupId) ON DELETE CASCADE,
                FOREIGN KEY (articleId) REFERENCES Articles(id)
            );
        """;

        // Execute the SQL statements to create the tables
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createArticlesTable);
            stmt.execute(createGroupsTable);
            stmt.execute(createGroupUsersTable);
            stmt.execute(createGroupArticlesTable);
        }
    }

    // Encrypts content using Base64 encoding
    public static String encryptContent(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    // Decrypts content encoded with Base64
    public static String decryptContent(String encryptedContent) {
        if (encryptedContent == null || encryptedContent.isEmpty()) return ""; // Return empty string if content is null or empty
        return new String(Base64.getDecoder().decode(encryptedContent), StandardCharsets.UTF_8);
    }

    // Adds an article to the Articles table
    public void addArticle(String title, String authors, String abstractText, String keywords, String body, String references, boolean isEncrypted) throws SQLException {
        String sql = "INSERT INTO Articles (title, authors, abstractText, keywords, body, references, isEncrypted) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, authors);
            pstmt.setString(3, abstractText);
            pstmt.setString(4, keywords);
            pstmt.setString(5, body);
            pstmt.setString(6, references);
            pstmt.setBoolean(7, isEncrypted);
            pstmt.executeUpdate(); // Execute the insert statement
        }
    }

    // Retrieves a list of articles with their basic information (ID, title, authors)
    public List<String> listArticles() throws SQLException {
        String sql = "SELECT id, title, authors FROM Articles ORDER BY id";
        List<String> articles = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int displayId = 1; // Display ID for user-facing output
            while (rs.next()) {
                articles.add("ID: " + displayId++ + ", Title: " + rs.getString("title") + ", Authors: " + rs.getString("authors"));
            }
        }
        return articles;
    }
    
    public List<String> getAdminAccounts() throws SQLException {
        String sql = "SELECT username FROM AccessRights WHERE canAdmin = TRUE";
        List<String> admins = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                admins.add(rs.getString("username"));
            }
        }
        return admins;
    } 

    // Retrieves detailed information about an article by its ID
    public String viewArticle(int articleId) throws SQLException {
        String sql = "SELECT * FROM Articles WHERE id = ?";
        StringBuilder articleDetails = new StringBuilder();

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, articleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean isEncrypted = rs.getBoolean("isEncrypted");
                    String body = isEncrypted ? decryptContent(rs.getString("body")) : rs.getString("body");

                    articleDetails.append("ID: ").append(articleId)
                            .append("\nTitle: ").append(rs.getString("title"))
                            .append("\nAuthors: ").append(rs.getString("authors"))
                            .append("\nAbstract: ").append(rs.getString("abstractText"))
                            .append("\nKeywords: ").append(rs.getString("keywords"))
                            .append("\nBody: ").append(body)
                            .append("\nReferences: ").append(rs.getString("references"));
                } else {
                    return "Article not found."; // Return message if article is not found
                }
            }
        }
        return articleDetails.toString();
    }

    // Deletes an article by its display ID
    public void deleteArticle(int displayId) throws SQLException {
        int articleId = getDatabaseIdForDisplayId(displayId); // Map display ID to database ID

        // Delete the article from GroupArticles table
        String deleteFromGroupArticlesSQL = "DELETE FROM GroupArticles WHERE articleId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFromGroupArticlesSQL)) {
            pstmt.setInt(1, articleId);
            pstmt.executeUpdate(); // Execute the delete statement
        }

        // Delete the article from Articles table
        String deleteFromArticlesSQL = "DELETE FROM Articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFromArticlesSQL)) {
            pstmt.setInt(1, articleId);
            pstmt.executeUpdate(); // Execute the delete statement
        }
    }

    private int getDatabaseIdForDisplayId(int displayId) throws SQLException {
        // Retrieves the database ID corresponding to the user-facing display ID
        String sql = "SELECT id FROM Articles ORDER BY id";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int currentDisplayId = 1;
            while (rs.next()) {
                if (currentDisplayId++ == displayId) return rs.getInt("id"); // Match display ID with database ID
            }
        }
        throw new SQLException("Invalid display ID: " + displayId); // Throw exception if no match is found
    }

    public void backupArticles(String backupFileName) throws SQLException {
        // Creates a backup of the Articles table to the specified file
        String backupSQL = String.format("SCRIPT TO '%s'", backupFileName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(backupSQL); // Execute the backup command
        }
    }

    public void restoreArticles(String backupFileName) throws SQLException {
        // Restores the Articles table from the specified backup file
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Articles"); // Drop existing table if it exists
            stmt.execute(String.format("RUNSCRIPT FROM '%s'", backupFileName)); // Restore from backup
        }
    }

    public void createGroup(String groupName, boolean isSpecialGroup) throws SQLException {
        // Creates a new group in the SpecialAccessGroups table
        String groupId = UUID.randomUUID().toString(); // Generate a unique group ID
        String groupType = isSpecialGroup ? "Special" : "General"; // Determine group type
        String sql = "INSERT INTO SpecialAccessGroups (groupId, groupName, groupType) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); // Set group ID
            pstmt.setString(2, groupName); // Set group name
            pstmt.setString(3, groupType); // Set group type
            pstmt.executeUpdate(); // Execute the insert statement
        }
    }

    public String getGroupIdByName(String groupName) throws SQLException {
        // Retrieves the group ID corresponding to the given group name
        String sql = "SELECT groupId FROM SpecialAccessGroups WHERE groupName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupName); // Set the group name parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("groupId"); // Return group ID if found
            }
        }
        throw new SQLException("Group not found: " + groupName); // Throw exception if no match is found
    }

    public void addUserToGroup(String groupId, String username, String role) throws SQLException {
        // Adds a user to a group in the GroupUsers table
        String sql = """
            MERGE INTO GroupUsers (groupId, username, role, canView, canAdmin)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); // Set group ID
            pstmt.setString(2, username); // Set username
            pstmt.setString(3, role); // Set user role
            pstmt.setBoolean(4, true); // Default to allowing view access
            pstmt.setBoolean(5, role.equalsIgnoreCase("Instructor")); // Grant admin rights if role is Instructor
            pstmt.executeUpdate(); // Execute the merge statement
        }
    }

    public boolean deleteUserFromGroup(String groupId, String username) throws SQLException {
        // Deletes a user from a group in the GroupUsers table
        String deleteSQL = "DELETE FROM GroupUsers WHERE groupId = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, groupId); // Set group ID
            pstmt.setString(2, username); // Set username
            return pstmt.executeUpdate() > 0; // Return true if a row was deleted
        }
    }

    public List<Map<String, String>> getUsersInGroup(String groupId) throws SQLException {
        // Retrieves a list of users in the specified group
        String sql = "SELECT username, role, canView, canAdmin FROM GroupUsers WHERE groupId = ?";
        List<Map<String, String>> users = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); // Set group ID
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> user = new HashMap<>(); // Create a map for user details
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    user.put("canView", rs.getBoolean("canView") ? "Yes" : "No");
                    user.put("canAdmin", rs.getBoolean("canAdmin") ? "Yes" : "No");
                    users.add(user); // Add user details to the list
                }
            }
        }
        return users; // Return the list of users
    }

    public void updateUserViewRights(String groupId, String username, boolean canView) throws SQLException {
        // Updates the view rights for a user in a group
        String sql = "UPDATE GroupUsers SET canView = ? WHERE groupId = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, canView); // Set the canView value
            pstmt.setString(2, groupId); // Set group ID
            pstmt.setString(3, username); // Set username
            pstmt.executeUpdate(); // Execute the update statement
        }
    }

    public void updateUserAdminRights(String groupId, String username, boolean canAdmin) throws SQLException {
        // Updates the admin rights for a user in a group
        int adminCount = countAdminsInGroup(groupId); // Count current admins in the group
        if (!canAdmin && adminCount == 1) {
            // Prevent removing admin rights if only one admin exists
            throw new SQLException("There must be at least one admin in the group.");
        }

        String sql = "UPDATE GroupUsers SET canAdmin = ? WHERE groupId = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, canAdmin); // Set the canAdmin value
            pstmt.setString(2, groupId); // Set group ID
            pstmt.setString(3, username); // Set username
            pstmt.executeUpdate(); // Execute the update statement
        }
    }

    private int countAdminsInGroup(String groupId) throws SQLException {
        // Counts the number of admin users in a specific group
        String sql = "SELECT COUNT(*) AS adminCount FROM GroupUsers WHERE groupId = ? AND canAdmin = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); // Set the group ID parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("adminCount"); // Return the admin count
            }
        }
        return 0; // Return 0 if no admin users are found
    }

    public void addArticleToGroup(String groupId, int articleId, boolean isEncrypted) throws SQLException {
        // Adds an article to a group after verifying the article exists

        // Check if the article exists in the Articles table
        String checkArticleSql = "SELECT id FROM Articles WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkArticleSql)) {
            checkStmt.setInt(1, articleId); // Set the article ID parameter
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) throw new SQLException("Article with ID " + articleId + " does not exist.");
            }
        }

        // Insert the article into the GroupArticles table
        String sql = "INSERT INTO GroupArticles (groupId, articleId) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); // Set the group ID parameter
            pstmt.setInt(2, articleId); // Set the article ID parameter
            pstmt.executeUpdate(); // Execute the insert statement
        }
    }

    public void deleteGroup(String groupId) throws SQLException {
        // Deletes a group by its group ID
        String deleteGroupSQL = "DELETE FROM SpecialAccessGroups WHERE groupId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroupSQL)) {
            pstmt.setString(1, groupId); // Set the group ID parameter
            if (pstmt.executeUpdate() == 0) throw new SQLException("No group found with ID: " + groupId); // Handle group not found
        }
    }

    public List<Map<String, String>> getArticlesInGroup(String groupId, String username) throws SQLException {
        // Retrieves a list of articles in a group that a specific user can access
        String sql = """
            SELECT a.id, a.title, a.body, a.isEncrypted, gu.canView
            FROM Articles a
            JOIN GroupArticles ga ON a.id = ga.articleId
            JOIN GroupUsers gu ON ga.groupId = gu.groupId
            WHERE ga.groupId = ? AND gu.username = ?
        """;

        List<Map<String, String>> articles = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); // Set the group ID parameter
            pstmt.setString(2, username); // Set the username parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> article = new HashMap<>();
                    article.put("id", String.valueOf(rs.getInt("id"))); // Article ID
                    article.put("title", rs.getString("title")); // Article title

                    boolean canView = rs.getBoolean("canView"); // Check if user has view permissions
                    boolean isEncrypted = rs.getBoolean("isEncrypted"); // Check if the article is encrypted
                    String body = rs.getString("body"); // Article body

                    if (canView) { 
                        body = isEncrypted ? decryptContent(body) : body; // Decrypt if encrypted
                    } else { 
                        body = "No Permission"; // Restrict access if no view permissions
                    }

                    article.put("body", body); // Add body to the map
                    articles.add(article); // Add the article to the list
                }
            }
        }
        return articles; // Return the list of articles
    }

    public List<String> searchArticles(String query, String level, String group) throws SQLException {
        // Searches for articles based on query text, content level, and group
        StringBuilder sql = new StringBuilder("SELECT id, title, authors, abstractText FROM Articles WHERE 1=1");
        List<Object> parameters = new ArrayList<>(); // List to store query parameters

        // Add conditions for query text if provided
        if (!query.isEmpty()) {
            sql.append(" AND (title LIKE ? OR authors LIKE ? OR abstractText LIKE ?)");
            String likeQuery = "%" + query + "%";
            parameters.add(likeQuery); parameters.add(likeQuery); parameters.add(likeQuery);
        }

        // Add condition for content level if specified
        if (!"All".equalsIgnoreCase(level)) {
            sql.append(" AND keywords LIKE ?");
            parameters.add("%" + level + "%");
        }

        // Add condition for group if specified
        if (!"All".equalsIgnoreCase(group)) {
            sql.append(" AND id IN (SELECT articleId FROM GroupArticles WHERE groupId = ?)");
            parameters.add(group);
        }

        sql.append(" ORDER BY id"); // Order results by ID

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) pstmt.setObject(i + 1, parameters.get(i)); // Set query parameters

            List<String> results = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                int sequence = 1; // Sequence number for display
                while (rs.next()) {
                    results.add(String.format("Seq: %d, Title: %s, Authors: %s, Abstract: %s",
                            sequence++, rs.getString("title"), rs.getString("authors"), rs.getString("abstractText")));
                }
            }
            return results; // Return the list of search results
        }
    }

    public String getLevelStatistics(List<String> articleIds) throws SQLException {
        // Generates statistics on content levels for a list of article IDs
        if (articleIds.isEmpty()) return "No articles to analyze."; // Handle empty list

        // Construct SQL query to fetch keywords for the specified article IDs
        String sql = "SELECT keywords FROM Articles WHERE id IN (" +
                articleIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";
        int beginnerCount = 0, intermediateCount = 0, advancedCount = 0, expertCount = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < articleIds.size(); i++) {
                pstmt.setInt(i + 1, Integer.parseInt(articleIds.get(i))); // Set article IDs as parameters
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String keywords = rs.getString("keywords").toLowerCase();
                    if (keywords.contains("beginner")) beginnerCount++;
                    if (keywords.contains("intermediate")) intermediateCount++;
                    if (keywords.contains("advanced")) advancedCount++;
                    if (keywords.contains("expert")) expertCount++;
                }
            }
        }

        // Return formatted statistics
        return String.format("Beginner: %d, Intermediate: %d, Advanced: %d, Expert: %d",
                beginnerCount, intermediateCount, advancedCount, expertCount);
    }

    public void clearDatabase() throws SQLException {
        // Clears all data from the database tables in the correct order to handle dependencies
        String[] tables = {
            "GroupArticles",    // Depends on Groups and Articles
            "GroupUsers",       // Depends on Groups and Users
            "SpecialAccessGroups", // Groups table
            "Articles"          // Articles table
        };

        // Iterate through each table and clear its data
        try (Statement stmt = connection.createStatement()) {
            for (String table : tables) {
                String sql = "DELETE FROM " + table; // Construct delete query for each table
                stmt.executeUpdate(sql); // Execute the delete statement
            }
        }
    }

}
