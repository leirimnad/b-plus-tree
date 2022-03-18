import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BPlusTree<K extends Comparable<K>, V> {
    private Node root;
    private final int maxChildren;

    public BPlusTree(int maxChildren) {
        this.maxChildren = maxChildren;
    }

    public V get(K key){
        LeafNode leaf = this.getLeaf(key);
        for (int i = 0; i < leaf.size; i++) {
            if (leaf.pairs.get(i).key.compareTo(key) == 0)
                return leaf.pairs.get(i).value;
        }
        return null;
    }

    public void insert(K key, V value){
        if (root == null) {
            root = new LeafNode(Stream.of(new Pair(key, value)).collect(Collectors.toList()), null, null, null);
            return;
        }

        LeafNode leaf = this.getLeaf(key);

        if (leaf.size < this.maxChildren-1){
            leaf.pairs.add(new Pair(key, value));
            leaf.size++;
        } else {
            insertSorted(leaf.pairs, key, value);
            breakNode(leaf);
        }
    }

    private void breakNode(LeafNode node){
        int toLeft = maxChildren/2;
        K splitKey = node.pairs.get(toLeft).key;
        LeafNode newRight = new LeafNode(new ArrayList<>(node.pairs.subList(toLeft, node.pairs.size())), node.parent, node, node.next);
        node.pairs = new ArrayList<>(node.pairs.subList(0, toLeft));
        node.size = toLeft;
        node.next = newRight;

        pushToParent(node, splitKey, newRight);
    }

    private void breakNode(InternalNode node){
        int toLeft = maxChildren/2;
        K splitKey = node.keys.get(toLeft);
        InternalNode newRight = new InternalNode(
                new ArrayList<>(node.keys.subList(toLeft+1, node.keys.size())),
                new ArrayList<>(node.pointers.subList(toLeft+1, node.pointers.size())),
                node.parent
        );
        node.keys = new ArrayList<>(node.keys.subList(0, toLeft));
        node.pointers = new ArrayList<>(node.pointers.subList(0, toLeft+1));
        node.size = toLeft;

        pushToParent(node, splitKey, newRight);
    }

    private void pushToParent(Node node, K splitKey, Node newRight){
        if (!node.equals(this.root)) {
            InternalNode parent = node.parent;
            int pos = insertSorted(parent.keys, splitKey);
            parent.pointers.add(pos+1, newRight);
            parent.size++;

            if (parent.size >= this.maxChildren)
                breakNode(parent);

        } else {
            InternalNode newRoot = new InternalNode(
                    Stream.of(splitKey).collect(Collectors.toList()),
                    Stream.of(node, newRight).collect(Collectors.toList()),
                    null
            );
            this.root = newRoot;
            node.parent = newRoot;
            newRight.parent = newRoot;
        }
    }

    private int insertSorted(List<K> list, K key){
        int index = Collections.binarySearch(list, key);
        if (index < 0) {
            index = -index - 1;
        }
        list.add(index, key);
        return index;
    }

    private void insertSorted(List<Pair> list, K key, V value){
        Pair newPair = new Pair(key, value);
        int index = Collections.binarySearch(list, newPair);
        if (index < 0) {
            index = -index - 1;
        }
        list.add(index, newPair);
    }

    private LeafNode getLeaf(K key){
        if (root.getClass().equals(LeafNode.class))
            return (LeafNode) root;

        InternalNode p = (InternalNode) root;

        while (true){
            Node next = null;
            for (int i = 0; i < p.size; i++) {
                if (key.compareTo(p.keys.get(i)) < 0){
                    next = p.pointers.get(i);
                    break;
                }
            }
            if (next == null)
                next = p.pointers.get(p.size);

            if (next.getClass().equals(LeafNode.class)) {
                return (LeafNode) next;
            } else {
                p = (InternalNode) next;
            }
        }
    }

    public void print(){
        Node c = this.root;
        while (c.getClass().equals(InternalNode.class)){
            c = ((InternalNode) c).pointers.get(0);
        }
        LeafNode l = (LeafNode) c;
        while (l != null){
            System.out.print("| ");
            for (Pair p : l.pairs) {
                System.out.print(p.key + ",");
            }
            System.out.print(" |");
            l = l.next;
        }
    }


    // Classes

    private class Pair implements Comparable<Pair> {

        public K key;
        public V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "<" +
                    key +
                    "," + value +
                    '>';
        }

        @Override
        public int compareTo(Pair o) {
            return this.key.compareTo(o.key);
        }
    }

    private class Node {
        InternalNode parent;
    }

    private class InternalNode extends Node {
        List<K> keys;
        int size;
        List<Node> pointers;

        public InternalNode(List<K> keys, List<Node> pointers, InternalNode parent) {
            if (keys.size() != pointers.size()-1)
                throw new IllegalArgumentException("Number of keys does not match with the number of pointers");
            this.keys = keys;
            this.size = keys.size();
            this.pointers = pointers;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return "InternalNode{" +
                    "" + keys +
                    '}';
        }
    }

    private class LeafNode extends Node{
        List<Pair> pairs;
        int size;
        LeafNode previous, next;

        public LeafNode(List<Pair> pairs, InternalNode parent, LeafNode previous, LeafNode next) {
            this.pairs = pairs;
            this.size = pairs.size();
            this.previous = previous;
            this.next = next;
            this.parent = parent;
        }

        @Override
        public String toString() {
            return "LeafNode{" +
                     pairs +
                    '}';
        }
    }

}











