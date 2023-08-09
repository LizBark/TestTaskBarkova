import org.example.WordCountStream;
import org.junit.jupiter.api.*;

import java.io.File;
import java.net.URL;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class testForTaskStream {
    @Test
    @DisplayName("Stream-Test for checking the correct number of words")
    public void testWordCount() {
        WordCountStream forTest = new WordCountStream();
        URL resource = getClass().getClassLoader().getResource("sampleA.txt");
        assertNotNull(resource);
        String filepath = new File(resource.getFile()).getAbsolutePath();
        Map<String, Integer> sortedMapAnswer = forTest.WordCountStreamMethod(filepath);

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
    @DisplayName("Stream-Test for checking the correct order of words")
    public void testWordCountSort () {
        WordCountStream forTest = new WordCountStream();
        URL resource = getClass().getClassLoader().getResource("sampleA.txt");
        assertNotNull(resource);
        String filepath = new File(resource.getFile()).getAbsolutePath();

        Map<String, Integer> sortedMapAnswer = forTest.WordCountStreamMethod(filepath);
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



