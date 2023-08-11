import org.example.WordCountDBSecond;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataBaseTestSecond {
    WordCountDBSecond wordCountDBSecond;
    @Test
    @DisplayName("Test for checking words count and sorting for first sample text (second ver)")
    public void testWordCountDBTwoFirstSample() throws ClassNotFoundException {
        URL resource = getClass().getClassLoader().getResource("sampleA.txt");
        assertNotNull(resource);

        String filepath = new File(resource.getFile()).getAbsolutePath();
        wordCountDBSecond = new WordCountDBSecond();
        wordCountDBSecond.WordCountDBMethodSecond(filepath);

        // Check word count for sample A
        assertEquals(7, getWordCountFromDb("chuck", filepath));
        assertEquals(7, getWordCountFromDb("norris", filepath));
        assertEquals(4, getWordCountFromDb("said", filepath));
        assertEquals(3, getWordCountFromDb("a", filepath));
        assertEquals(2, getWordCountFromDb("nothing", filepath));
        assertEquals(1, getWordCountFromDb("is", filepath));
        assertEquals(1, getWordCountFromDb("doesn't", filepath));

    }
    @Test
    @DisplayName("Test for checking words count and sorting for second sample text (second ver)")
    public void testWordCountDBTwoSecondSample() throws ClassNotFoundException {
        URL resource = getClass().getClassLoader().getResource("sampleB.txt");
        assertNotNull(resource);

        String filepath = new File(resource.getFile()).getAbsolutePath();
        wordCountDBSecond = new WordCountDBSecond();
        wordCountDBSecond.WordCountDBMethodSecond(filepath);

        // Check word count for sample B
        assertEquals(3, getWordCountFromDb("sample", filepath));
        assertEquals(9, getWordCountFromDb("chuck", filepath));
        assertEquals(7, getWordCountFromDb("norris", filepath));
        assertEquals(4, getWordCountFromDb("said", filepath));
        assertEquals(3, getWordCountFromDb("a", filepath));
        assertEquals(2, getWordCountFromDb("nothing", filepath));
        assertEquals(1, getWordCountFromDb("is", filepath));
        assertEquals(1, getWordCountFromDb("doesn't", filepath));

    }

    @Test
    @DisplayName("Check db (second ver)")
    public void testDatabaseFunctionalityTwo() throws SQLException, ClassNotFoundException {
        URL resource = getClass().getClassLoader().getResource("sampleA.txt");
        assertNotNull(resource);

        String filepath = new File(resource.getFile()).getAbsolutePath();
        wordCountDBSecond = new WordCountDBSecond();
        wordCountDBSecond.WordCountDBMethodSecond(filepath);

        URL resourceB = getClass().getClassLoader().getResource("sampleB.txt");
        assertNotNull(resource);

        String filepathB = new File(resourceB.getFile()).getAbsolutePath();
        wordCountDBSecond.WordCountDBMethodSecond(filepathB);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:WordCountsSecond.db")) {
            Statement statement = connection.createStatement();

            ResultSet rss = statement.executeQuery("SELECT COUNT(*) FROM BookScans");

            assertTrue(rss.next());
            // Two files scanned - two in BookScans
            assertEquals(2, rss.getInt(1));
        } catch (SQLException e) {
            System.err.println("Problem with db here with " + e.getMessage());
        }
    }

    private int getWordCountFromDb(String word, String filepath) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:WordCountsSecond.db")) {
            String selectBookIdSql = "SELECT id FROM BookScans WHERE book = ?";

            try (PreparedStatement ps = connection.prepareStatement(selectBookIdSql)) {
                ps.setString(1, filepath);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int bookId = rs.getInt("id");
                    String selectWordCountSql = "SELECT count FROM Words WHERE word = ? AND bookScanId = ?";

                    try (PreparedStatement psWordCount = connection.prepareStatement(selectWordCountSql)) {
                        psWordCount.setString(1, word);
                        psWordCount.setInt(2, bookId);
                        ResultSet rsCount = psWordCount.executeQuery();
                        if (rsCount.next()) {
                            return rsCount.getInt("count");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
        return 0;
    }

}