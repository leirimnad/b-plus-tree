public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(15, "Chocolates");
        tree.insert(20, "Fruits");
        tree.insert(25, "Salt food");
        tree.insert(35, "Soups");
        tree.insert(45, "Salads");
        tree.insert(55, "Salads");
        tree.insert(30, "Salt");

        tree.delete(45);
        tree.delete(35);
        tree.delete(25);
        tree.print();
    }
}