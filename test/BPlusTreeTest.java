public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(1, "Chocolates");
        tree.insert(3, "Fruits");
        tree.insert(2, "Salt food");
        tree.insert(4, "Soups");
        tree.insert(5, "Salads");
        tree.print();
    }
}