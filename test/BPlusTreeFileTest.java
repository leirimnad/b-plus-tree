import io.FileParser;
import tree.BPlusTree;

public class BPlusTreeFileTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        FileParser.runFile("test/test.txt", tree);
    }
}
