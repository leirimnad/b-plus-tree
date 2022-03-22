import tree.BPlusTree;
import util.RationalNumber;

public class BPlusTreeTest {
    public static void main(String[] args) {
        BPlusTree<RationalNumber, String> tree = new BPlusTree<>(5);
        tree.insert(new RationalNumber(3, 4), "Cakes");
        tree.insert(new RationalNumber(5, 4), "Chocolates");
        tree.insert(new RationalNumber(6, 5), "Fruits");
        tree.insert(new RationalNumber(2, 1), "Salt food");
        tree.insert(new RationalNumber(-24, 2), "Onion");
        tree.insert(new RationalNumber(9, 4), "Salt");
        tree.insert(new RationalNumber(11, 6), "Soups");
        tree.delete(new RationalNumber(-12, 1));
        tree.print();
    }
}