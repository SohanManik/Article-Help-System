package model;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class DatabaseHelper {

    private static DatabaseHelper instance;
    private Connection connection;

    private DatabaseHelper() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
        setupDatabase();
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) try {
            instance = new DatabaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return instance;
    }

    private void setupDatabase() throws SQLException {
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

        String createGroupsTable = """
            CREATE TABLE IF NOT EXISTS SpecialAccessGroups (
                groupId VARCHAR(255) PRIMARY KEY,
                groupName VARCHAR(255) UNIQUE NOT NULL,
                groupType VARCHAR(50) NOT NULL
            );
        """;

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

        String createGroupArticlesTable = """
            CREATE TABLE IF NOT EXISTS GroupArticles (
                groupId VARCHAR(255),
                articleId INT,
                PRIMARY KEY (groupId, articleId),
                FOREIGN KEY (groupId) REFERENCES SpecialAccessGroups(groupId) ON DELETE CASCADE,
                FOREIGN KEY (articleId) REFERENCES Articles(id)
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createArticlesTable);
            stmt.execute(createGroupsTable);
            stmt.execute(createGroupUsersTable);
            stmt.execute(createGroupArticlesTable);
        }
    }

    public static String encryptContent(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    public static String decryptContent(String encryptedContent) {
        if (encryptedContent == null || encryptedContent.isEmpty()) return "";
        return new String(Base64.getDecoder().decode(encryptedContent), StandardCharsets.UTF_8);
    }

    public void addArticle(String title, String authors, String abstractText, String keywords, String body, String references, boolean isEncrypted) throws SQLException {
        String sql = "INSERT INTO Articles (title, authors, abstractText, keywords, body, references, isEncrypted) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title); pstmt.setString(2, authors); pstmt.setString(3, abstractText);
            pstmt.setString(4, keywords); pstmt.setString(5, body); pstmt.setString(6, references); pstmt.setBoolean(7, isEncrypted);
            pstmt.executeUpdate();
        }
    }

    public List<String> listArticles() throws SQLException {
        String sql = "SELECT id, title, authors FROM Articles ORDER BY id";
        List<String> articles = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int displayId = 1;
            while (rs.next()) {
                articles.add("ID: " + displayId++ + ", Title: " + rs.getString("title") + ", Authors: " + rs.getString("authors"));
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
                    return "Article not found.";
                }
            }
        }
        return articleDetails.toString();
    }

    public void deleteArticle(int displayId) throws SQLException {
        int articleId = getDatabaseIdForDisplayId(displayId);
        String deleteFromGroupArticlesSQL = "DELETE FROM GroupArticles WHERE articleId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFromGroupArticlesSQL)) {
            pstmt.setInt(1, articleId);
            pstmt.executeUpdate();
        }

        String deleteFromArticlesSQL = "DELETE FROM Articles WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteFromArticlesSQL)) {
            pstmt.setInt(1, articleId);
            pstmt.executeUpdate();
        }
    }

    private int getDatabaseIdForDisplayId(int displayId) throws SQLException {
        String sql = "SELECT id FROM Articles ORDER BY id";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            int currentDisplayId = 1;
            while (rs.next()) {
                if (currentDisplayId++ == displayId) return rs.getInt("id");
            }
        }
        throw new SQLException("Invalid display ID: " + displayId);
    }

    public void backupArticles(String backupFileName) throws SQLException {
        String backupSQL = String.format("SCRIPT TO '%s'", backupFileName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(backupSQL);
        }
    }

    public void restoreArticles(String backupFileName) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Articles");
            stmt.execute(String.format("RUNSCRIPT FROM '%s'", backupFileName));
        }
    }

    public void createGroup(String groupName, boolean isSpecialGroup) throws SQLException {
        String groupId = UUID.randomUUID().toString();
        String groupType = isSpecialGroup ? "Special" : "General";
        String sql = "INSERT INTO SpecialAccessGroups (groupId, groupName, groupType) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); pstmt.setString(2, groupName); pstmt.setString(3, groupType);
            pstmt.executeUpdate();
        }
    }

    public String getGroupIdByName(String groupName) throws SQLException {
        String sql = "SELECT groupId FROM SpecialAccessGroups WHERE groupName = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getString("groupId");
            }
        }
        throw new SQLException("Group not found: " + groupName);
    }

    public void addUserToGroup(String groupId, String username, String role) throws SQLException {
        String sql = """
            MERGE INTO GroupUsers (groupId, username, role, canView, canAdmin)
            VALUES (?, ?, ?, ?, ?)
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); pstmt.setString(2, username); pstmt.setString(3, role);
            pstmt.setBoolean(4, true); pstmt.setBoolean(5, role.equalsIgnoreCase("Instructor"));
            pstmt.executeUpdate();
        }
    }

    public boolean deleteUserFromGroup(String groupId, String username) throws SQLException {
        String deleteSQL = "DELETE FROM GroupUsers WHERE groupId = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, groupId); pstmt.setString(2, username);
            return pstmt.executeUpdate() > 0;
        }
    }

    public List<Map<String, String>> getUsersInGroup(String groupId) throws SQLException {
        String sql = "SELECT username, role, canView, canAdmin FROM GroupUsers WHERE groupId = ?";
        List<Map<String, String>> users = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> user = new HashMap<>();
                    user.put("username", rs.getString("username"));
                    user.put("role", rs.getString("role"));
                    user.put("canView", rs.getBoolean("canView") ? "Yes" : "No");
                    user.put("canAdmin", rs.getBoolean("canAdmin") ? "Yes" : "No");
                    users.add(user);
                }
            }
        }
        return users;
    }

    public void updateUserViewRights(String groupId, String username, boolean canView) throws SQLException {
        String sql = "UPDATE GroupUsers SET canView = ? WHERE groupId = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, canView); pstmt.setString(2, groupId); pstmt.setString(3, username);
            pstmt.executeUpdate();
        }
    }

    public void updateUserAdminRights(String groupId, String username, boolean canAdmin) throws SQLException {
        int adminCount = countAdminsInGroup(groupId);
        if (!canAdmin && adminCount == 1) throw new SQLException("There must be at least one admin in the group.");

        String sql = "UPDATE GroupUsers SET canAdmin = ? WHERE groupId = ? AND username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBoolean(1, canAdmin); pstmt.setString(2, groupId); pstmt.setString(3, username);
            pstmt.executeUpdate();
        }
    }

    private int countAdminsInGroup(String groupId) throws SQLException {
        String sql = "SELECT COUNT(*) AS adminCount FROM GroupUsers WHERE groupId = ? AND canAdmin = TRUE";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return rs.getInt("adminCount");
            }
        }
        return 0;
    }

    public void addArticleToGroup(String groupId, int articleId, boolean isEncrypted) throws SQLException {
        String checkArticleSql = "SELECT id FROM Articles WHERE id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkArticleSql)) {
            checkStmt.setInt(1, articleId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) throw new SQLException("Article with ID " + articleId + " does not exist.");
            }
        }

        String sql = "INSERT INTO GroupArticles (groupId, articleId) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); pstmt.setInt(2, articleId);
            pstmt.executeUpdate();
        }
    }

    public void deleteGroup(String groupId) throws SQLException {
        String deleteGroupSQL = "DELETE FROM SpecialAccessGroups WHERE groupId = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteGroupSQL)) {
            pstmt.setString(1, groupId);
            if (pstmt.executeUpdate() == 0) throw new SQLException("No group found with ID: " + groupId);
        }
    }

    public List<Map<String, String>> getArticlesInGroup(String groupId, String username) throws SQLException {
        String sql = """
            SELECT a.id, a.title, a.body, a.isEncrypted, gu.canView
            FROM Articles a
            JOIN GroupArticles ga ON a.id = ga.articleId
            JOIN GroupUsers gu ON ga.groupId = gu.groupId
            WHERE ga.groupId = ? AND gu.username = ?
        """;

        List<Map<String, String>> articles = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, groupId); pstmt.setString(2, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, String> article = new HashMap<>();
                    article.put("id", String.valueOf(rs.getInt("id")));
                    article.put("title", rs.getString("title"));

                    boolean canView = rs.getBoolean("canView");
                    boolean isEncrypted = rs.getBoolean("isEncrypted");
                    String body = rs.getString("body");

                    if (canView) { body = isEncrypted ? decryptContent(body) : body;
                    } else { body = "No Permission"; }

                    article.put("body", body);
                    articles.add(article);
                }
            }
        }
        return articles;
    }

    public List<String> searchArticles(String query, String level, String group) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT id, title, authors, abstractText FROM Articles WHERE 1=1");
        List<Object> parameters = new ArrayList<>();

        if (!query.isEmpty()) {
            sql.append(" AND (title LIKE ? OR authors LIKE ? OR abstractText LIKE ?)");
            String likeQuery = "%" + query + "%";
            parameters.add(likeQuery); parameters.add(likeQuery); parameters.add(likeQuery);
        }

        if (!"All".equalsIgnoreCase(level)) {
            sql.append(" AND keywords LIKE ?");
            parameters.add("%" + level + "%");
        }

        if (!"All".equalsIgnoreCase(group)) {
            sql.append(" AND id IN (SELECT articleId FROM GroupArticles WHERE groupId = ?)");
            parameters.add(group);
        }

        sql.append(" ORDER BY id");

        try (PreparedStatement pstmt = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) pstmt.setObject(i + 1, parameters.get(i));

            List<String> results = new ArrayList<>();
            try (ResultSet rs = pstmt.executeQuery()) {
                int sequence = 1;
                while (rs.next()) {
                    results.add(String.format("Seq: %d, Title: %s, Authors: %s, Abstract: %s",
                            sequence++, rs.getString("title"), rs.getString("authors"), rs.getString("abstractText")));
                }
            }
            return results;
        }
    }

    public String getLevelStatistics(List<String> articleIds) throws SQLException {
        if (articleIds.isEmpty()) return "No articles to analyze.";

        String sql = "SELECT keywords FROM Articles WHERE id IN (" +
                articleIds.stream().map(id -> "?").collect(Collectors.joining(", ")) + ")";
        int beginnerCount = 0, intermediateCount = 0, advancedCount = 0, expertCount = 0;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < articleIds.size(); i++) {
                pstmt.setInt(i + 1, Integer.parseInt(articleIds.get(i)));
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

        return String.format("Beginner: %d, Intermediate: %d, Advanced: %d, Expert: %d",
                beginnerCount, intermediateCount, advancedCount, expertCount);
    }
    
////////////////////////////////////////////////////////////////////////////////////////
    public void clearDatabase() throws SQLException {
        // List the tables in the correct order to handle dependencies
        String[] tables = {
            "GroupArticles",    // Depends on Groups and Articles
            "GroupUsers",       // Depends on Groups and Users
            "SpecialAccessGroups", // Groups table
            "Articles"          // Articles table
        };

        // Iterate through each table and clear its data
        try (Statement stmt = connection.createStatement()) {
            for (String table : tables) {
                String sql = "DELETE FROM " + table;
                stmt.executeUpdate(sql);
            }
        }
    }

}
