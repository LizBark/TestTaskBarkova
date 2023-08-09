import org.example.WordCountDB;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
public class DataBaseTest {
    WordCountDB wordCountDB;

    @Test
    @DisplayName("Test for checking words count and sorting for first sample text")
    public void testWordCountDBFirst() {
        wordCountDB = new WordCountDB();
        URL resource = getClass().getClassLoader().getResource("sampleA.txt");
        assertNotNull(resource);
        String filepath = new File(resource.getFile()).getAbsolutePath();

        Map<String, Integer> sortedMapAnswer = wordCountDB.WordCountDBMethod(filepath);

        // Check word count for sample A
        assertEquals(7, sortedMapAnswer.get("chuck").intValue());
        assertEquals(7, sortedMapAnswer.get("chuck").intValue());
        assertEquals(7, sortedMapAnswer.get("norris").intValue());
        assertEquals(4, sortedMapAnswer.get("said").intValue());
        assertEquals(3, sortedMapAnswer.get("a").intValue());
        assertEquals(2, sortedMapAnswer.get("nothing").intValue());
        assertEquals(2, sortedMapAnswer.get("when").intValue());
        assertEquals(1, sortedMapAnswer.get("is").intValue());
        assertEquals(1, sortedMapAnswer.get("doesn't").intValue());

        // Check sorting
        checkSorting(sortedMapAnswer);
    }

    @Test
    @DisplayName("Test for checking words count and sorting for second sample text")
    public void testWordCountDBSecond() {
        wordCountDB = new WordCountDB();
        URL resource = getClass().getClassLoader().getResource("sampleB.txt");
        assertNotNull(resource);

        String filepath = new File(resource.getFile()).getAbsolutePath();
        Map<String, Integer> sortedMapAnswer = wordCountDB.WordCountDBMethod(filepath);

        // Check word count for sample B
        assertEquals(3, sortedMapAnswer.get("sample").intValue());
        assertEquals(9, sortedMapAnswer.get("chuck").intValue());
        assertEquals(7, sortedMapAnswer.get("norris").intValue());
        assertEquals(4, sortedMapAnswer.get("said").intValue());
        assertEquals(3, sortedMapAnswer.get("a").intValue());
        assertEquals(2, sortedMapAnswer.get("nothing").intValue());
        assertEquals(2, sortedMapAnswer.get("when").intValue());
        assertEquals(1, sortedMapAnswer.get("doesn't").intValue());

        // Check sorting
        checkSorting(sortedMapAnswer);
    }

    private void checkSorting(Map<String, Integer> sortedMapAnswer) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(sortedMapAnswer.entrySet());

        for (int i = 0; i < entries.size() - 1; i++) {
            Map.Entry<String, Integer> current = entries.get(i);
            Map.Entry<String, Integer> next = entries.get(i + 1);

            // Check count ordering
            assertTrue(current.getValue() >= next.getValue());

            // Check alphabetical ordering
            if (current.getValue().equals(next.getValue())) {
                assertTrue(current.getKey().compareTo(next.getKey()) < 0);
            }
        }
    }

    @Test
    @DisplayName("Check db")
    public void testDatabaseFunctionality() throws ClassNotFoundException {
        wordCountDB = new WordCountDB();
        // Sample A
        URL resourceA = getClass().getClassLoader().getResource("sampleA.txt");
        assertNotNull(resourceA);
        String filepathA = new File(resourceA.getFile()).getAbsolutePath();

        // Sample B
        URL resourceB = getClass().getClassLoader().getResource("sampleB.txt");
        assertNotNull(resourceB);
        String filepathB = new File(resourceB.getFile()).getAbsolutePath();

        Map<String, Integer> sortedMapA = wordCountDB.WordCountDBMethod(filepathA);
        WordCountDB.createNewDB(sortedMapA, filepathA);

        Map<String, Integer> sortedMapB = wordCountDB.WordCountDBMethod(filepathB);
        WordCountDB.createNewDB(sortedMapB, filepathB);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:WordCounts.db")) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM BookScans");
            assertTrue(rs.next());
            // Two files scanned - two in BookScans
            assertEquals(2, rs.getInt(1));

        } catch (SQLException e) {
            System.out.println("Problem with db " + e.getMessage());
        }
    }
}
