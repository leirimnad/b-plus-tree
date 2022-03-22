import tree.BPlusTree;

public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(5);
        tree.insert(1, "Cakes");
        tree.insert(6, "Chocolates");
        tree.insert(9, "Fruits");
        tree.insert(14, "Salt food");
        tree.insert(7, "Salt");
        tree.insert(2, "Soups");
        tree.insert(8, "Cakes");
        tree.insert(12, "Cakes");
        tree.insert(13, "Cakes");
        tree.insert(3, "Cakes");
        tree.insert(10, "Cakes");
        tree.insert(0, "Cakes");
        tree.insert(4, "Cakes");
        tree.insert(5, "Cakes");
        tree.insert(11, "Cakes");

        tree.delete(4);
        tree.print();

        tree.delete(7);
        tree.print();

        tree.delete(12);
        tree.print();

        tree.delete(1);
        tree.delete(8);

        tree.print();

    }
}