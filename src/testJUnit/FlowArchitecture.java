package testJUnit;

import model.DatabaseHelper;
import org.junit.jupiter.api.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FlowArchitecture {

    private DatabaseHelper databaseHelper;

    @BeforeAll
    void setup() throws SQLException {
        databaseHelper = DatabaseHelper.getInstance();
        System.out.println("Initialized DatabaseHelper instance.");
    }

    @BeforeEach
    void clearDatabase() throws SQLException {
        databaseHelper.clearDatabase();
        System.out.println("Database cleared before running the test.");
    }

    @Test
    void testAddArticleIntegrity() throws SQLException {
        System.out.println("Running testAddArticleIntegrity...");

        // Arrange
        String title = "Sample Title";
        String authors = "Author1, Author2";
        String abstractText = "This is a sample abstract.";
        String keywords = "keyword1, keyword2";
        String body = "This is the body of the article.";
        String references = "Ref1, Ref2";
        boolean isEncrypted = false;

        System.out.println("Adding an article with the title: " + title);

        // Act
        databaseHelper.addArticle(title, authors, abstractText, keywords, body, references, isEncrypted);
        var articles = databaseHelper.listArticles();

        System.out.println("Retrieved articles from the database: " + articles);

        // Assert
        assertNotNull(articles, "Articles list should not be null.");
        assertEquals(1, articles.size(), "There should be exactly one article in the database.");
        assertTrue(articles.get(0).contains(title), "The article title should be present in the articles list.");
        assertTrue(articles.get(0).contains(authors), "The authors should be present in the articles list.");

        System.out.println("testAddArticleIntegrity passed.");
    }

    @Test
    void testClearDatabase() throws SQLException {
        System.out.println("Running testClearDatabase...");

        // Arrange
        databaseHelper.addArticle("Test Title", "Author", "Abstract", "Keywords", "Body", "References", false);
        System.out.println("Added an article to test database clearing.");

        // Act
        databaseHelper.clearDatabase();
        var articles = databaseHelper.listArticles();

        System.out.println("Database cleared. Retrieved articles: " + articles);

        // Assert
        assertNotNull(articles, "Articles list should not be null.");
        assertEquals(0, articles.size(), "Database should be empty after clearing.");

        System.out.println("testClearDatabase passed.");
    }

    @Test
    void testEncryptionFlow() throws SQLException {
        System.out.println("Running testEncryptionFlow...");

        // Arrange
        String body = "Sensitive data";
        boolean isEncrypted = true;

        System.out.println("Adding an encrypted article with body: " + body);

        // Act
        databaseHelper.addArticle("Encrypted Article", "Author", "Abstract", "Keywords", body, "References", isEncrypted);
        String articleDetails = databaseHelper.viewArticle(1); // Assuming the ID is 1 after insertion.

        System.out.println("Retrieved article details: " + articleDetails);

        // Assert
        assertNotNull(articleDetails, "Article details should not be null.");
        assertTrue(articleDetails.contains("Encrypted Article"), "The article title should be in the details.");
        assertFalse(articleDetails.contains(body), "The raw body content should not be directly visible.");
        assertTrue(articleDetails.contains(DatabaseHelper.decryptContent(DatabaseHelper.encryptContent(body))),
                "Decrypted content should match the original body.");

        System.out.println("testEncryptionFlow passed.");
    }
}
