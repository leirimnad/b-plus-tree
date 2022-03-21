public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(15, "Chocolates");
        tree.insert(20, "Fruits");
        tree.insert(25, "Salt food");
        tree.delete(20);
        tree.insert(35, "Soups");
        tree.insert(45, "Salads");
        tree.delete(15);
        tree.delete(45);
        tree.insert(45, "Salads");
        tree.insert(55, "Salads");
        tree.insert(425, "Salads");
        tree.insert(145, "Salads");
        tree.insert(2, "Salads");
        tree.print();
        tree.insert(11, "Salads");
        tree.insert(30, "Salt");
        tree.print();


        tree.delete(55);
        tree.print();

        tree.delete(35);
        tree.print();

        tree.delete(25);
        tree.print();
    }
}