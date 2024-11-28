package testJUnit;
//Import statements
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import model.DatabaseHelper;
import java.sql.SQLException;
import java.util.List;

public class DatabaseHelperTest {

 private DatabaseHelper databaseHelper;

 @BeforeEach
 void setUp() throws SQLException {
     System.out.println("Setting up test environment...");

     // Initialize DatabaseHelper instance
     databaseHelper = DatabaseHelper.getInstance();

     // Clear existing data to ensure test isolation
     System.out.println("Clearing database...");
     databaseHelper.clearDatabase();

     // Add sample articles to the database
     System.out.println("Adding sample articles to the database...");
     databaseHelper.addArticle("Java Basics", "Author A", "Introduction to Java", "Beginner,Java", "Content of Java Basics", "Ref1", false);
     databaseHelper.addArticle("Advanced Java", "Author B", "Deep dive into Java", "Advanced,Java", "Content of Advanced Java", "Ref2", false);
     databaseHelper.addArticle("Java Performance", "Author C", "Optimizing Java applications", "Intermediate,Java,Performance", "Content of Java Performance", "Ref3", false);

     System.out.println("Test setup complete.\n");
 }

 @AfterEach
 void tearDown() throws SQLException {
     System.out.println("Tearing down test environment...");
     databaseHelper.clearDatabase();
     System.out.println("Database cleared.\n");
 }

 @Test
 void testSearchArticles_PerformanceAndAccuracy() throws SQLException {
     System.out.println("Starting test: testSearchArticles_PerformanceAndAccuracy");
     System.out.println("Performing search with keyword 'Java', level 'All', and group 'All'...");

     // Start time measurement
     long startTime = System.currentTimeMillis();

     // Perform search with keyword "Java" and level "All" in group "All"
     List<String> results = databaseHelper.searchArticles("Java", "All", "All");

     // End time measurement
     long endTime = System.currentTimeMillis();
     long duration = endTime - startTime;

     System.out.println("Search completed in " + duration + "ms.");
     System.out.println("Search results:");
     results.forEach(System.out::println);

     // Expected number of results
     int expectedResults = 3;

     // Assert that the correct number of results is returned
     System.out.println("Verifying result count...");
     assertEquals(expectedResults, results.size(), "Expected " + expectedResults + " articles containing 'Java'");
     System.out.println("Result count verified: " + results.size() + " articles found.");

     // Assert that the search operation completes within 2000 milliseconds (2 seconds)
     System.out.println("Verifying performance...");
     assertTrue(duration < 2000, "Search operation took too long: " + duration + "ms");
     System.out.println("Performance verified: Search completed within 2 seconds.");

     // Optionally, assert the content of the results
     System.out.println("Verifying result contents...");
     assertTrue(results.get(0).contains("Java Basics"), "Expected result to contain 'Java Basics'");
     assertTrue(results.get(1).contains("Advanced Java"), "Expected result to contain 'Advanced Java'");
     assertTrue(results.get(2).contains("Java Performance"), "Expected result to contain 'Java Performance'");
     System.out.println("Result contents verified.");

     System.out.println("Test testSearchArticles_PerformanceAndAccuracy completed successfully.\n");
 }
}
