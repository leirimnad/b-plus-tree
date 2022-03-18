public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(5, "Chocolates");
        tree.insert(15, "Fruits");
        tree.insert(25, "Salt food");
        tree.insert(35, "Soups");
        tree.insert(45, "Salads");
        System.out.println(tree.get(5));
        tree.print();
    }
}