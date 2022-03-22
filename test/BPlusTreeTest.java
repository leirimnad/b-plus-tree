import tree.BPlusTree;

public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<Integer, String> tree = new BPlusTree<>(3);
        tree.insert(5, "Cakes");
        tree.insert(15, "Chocolates");
        tree.insert(20, "Fruits");
        tree.insert(25, "Salt food");
        tree.insert(30, "Salt");
        tree.insert(35, "Soups");
        tree.insert(40, "Salads");
        tree.insert(45, "Salads");
        tree.insert(55, "Salads");

        tree.delete(40);
        tree.delete(45);
        tree.delete(35);
        tree.delete(20);
        tree.delete(30);
        tree.delete(55);
        tree.delete(15);
        tree.delete(25);
        tree.delete(5);
        tree.insert(1, "RRR");
        tree.insert(2, "RRR");
        tree.insert(3, "RRR");
        tree.print();
    }
}