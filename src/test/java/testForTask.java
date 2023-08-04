import org.example.Main;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class testForTask {
    @Test
    @DisplayName("Test for checking the correct number of words")
    public void testWordCount() {
        String filepath = "C:/Users/ElizavetaBarkova/IdeaProjects/TestTaskT/src/main/java/org/example/sample.txt" ;
        Main forTest = new Main();
        Map<String, Integer> sortedMapAnswer = forTest.WordCount(filepath);

        assertEquals(7, sortedMapAnswer.get("chuck").intValue());
        assertEquals(7, sortedMapAnswer.get("norris").intValue());
        assertEquals(4, sortedMapAnswer.get("said").intValue());
        assertEquals(3, sortedMapAnswer.get("a").intValue());
        assertEquals(2, sortedMapAnswer.get("nothing").intValue());
        assertEquals(2, sortedMapAnswer.get("when").intValue());
        assertEquals(1, sortedMapAnswer.get("is").intValue());
        assertEquals(1, sortedMapAnswer.get("doesn't").intValue());

    }
    @Test
    @DisplayName("Test for checking the correct order of words")
    public void testWordCountSort() {
        String filepath = "C:/Users/ElizavetaBarkova/IdeaProjects/TestTaskT/src/main/java/org/example/sample.txt" ;
        Main forTest = new Main();
        Map<String, Integer> sortedMapAnswer = forTest.WordCount(filepath);
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

}

