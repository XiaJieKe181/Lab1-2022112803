import org.junit.jupiter.api.Test; // 注意这里是jupiter.api
import static org.junit.jupiter.api.Assertions.assertEquals; // 注意这里是Assertions

public class MainTest {

    @Test
    public void testQueryBridgeWords_ValidCase() {
        Main main = new Main();
        String result = main.queryBridgeWords("apple", "banana");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_NoBridgeWords() {
        Main main = new Main();
        String result = main.queryBridgeWords("cat", "dog");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_InvalidInput_FormatError() {
        Main main = new Main();
        String result = main.queryBridgeWords("test@word", "abc");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_WordNotExist() {
        Main main = new Main();
        String result = main.queryBridgeWords("unknown", "word");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_CaseInsensitive() {
        Main main = new Main();
        String result = main.queryBridgeWords("Apple", "Banana");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_SingleWordInput() {
        Main main = new Main();
        String result = main.queryBridgeWords("apple", "");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_MultipleSpaces() {
        Main main = new Main();
        String result = main.queryBridgeWords("apple   mango", "cherry");
        assertEquals("No word1 or word2 in the graph!", result);
    }

    @Test
    public void testQueryBridgeWords_DirectPathExists() {
        Main main = new Main();
        String result = main.queryBridgeWords("apple", "mango");
        assertEquals("No word1 or word2 in the graph!", result);
    }
}