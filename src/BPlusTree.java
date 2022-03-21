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

        leaf.addEntry(key, value);

        if (leaf.isOverfed())
            breakNode(leaf);

    }

    public void delete(K key){
        LeafNode leaf = getLeaf(key);

        K inorderSuccessor = leaf.inorderSuccessor(key);
        leaf.removeEntry(key);

        fixUnderfeeding(leaf, key, inorderSuccessor);
    }

    private void fixUnderfeeding(LeafNode node, K deleting, K inorderSuccessor){
        if (!node.isUnderfed()) {
            if (inorderSuccessor != null)
                replacePropagate(node.parent, deleting, inorderSuccessor);
            return;
        }

        // try to steal
        if (node.hasNext() && node.parent.equals(node.next().parent) && node.next().hasSpareEntries())
            stealFromNext(node, deleting);
        else if (node.hasPrevious() && node.parent.equals(node.previous().parent) && node.previous().hasSpareEntries())
            stealFromPrevious(node, deleting);

        // else merge
        else {
            if (node.hasNext() && node.next().parent.equals(node.parent))
                merge(node, node.next(), deleting, inorderSuccessor);
            else
                merge(node.previous(), node, deleting, inorderSuccessor);
        }
    }

    private void fixUnderfeeding(InternalNode node, K deleting){
        if (!node.isUnderfed())
            return;

        if (node.equals(this.root)){
            this.root = node.pointers.get(0);
            this.root.parent = null;
            return;
        }

        InternalNode next = node.next();
        InternalNode previous = node.previous();

        // try to steal
        if (next != null && node.parent.equals(next.parent) && next.hasSpareEntries())
            stealFromNext(node, deleting);
        else if (previous != null && node.parent.equals(previous.parent) && previous.hasSpareEntries())
            stealFromPrevious(node, deleting);

        // else merge
        else {
            if (next != null)
                merge(node, next);
            else if (previous != null)
                merge(previous, node);
            else
                throw new RuntimeException("Node has no previous and no next");
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
        for (Node child : newRight.pointers)
            child.parent = newRight;
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

    private void insertSorted(List<Pair> list, Pair pair){
        int index = Collections.binarySearch(list, pair);
        if (index < 0) {
            index = -index - 1;
        }
        list.add(index, pair);
    }

    private void stealFromNext(LeafNode node, K deleting){
        Pair toMove = node.next.removeAt(0);
        node.addEntry(toMove);
        node.parent.keys.set(Collections.binarySearch(node.parent.keys, toMove.key), node.next.pairs.get(0).key);
        replacePropagate(node.parent, deleting, toMove.key);
    }

    private void stealFromNext(InternalNode node, K deleting){
        InternalNode next = node.next();
        if (next == null)
            throw new IllegalArgumentException("Node has no next");

        int keyNum = node.parent.childNum(node);
        K fromParent = node.parent.keys.get(keyNum);

        K stolenKey = next.keys.remove(0);
        Node stolenChild = next.pointers.remove(0);
        next.size--;

        node.parent.keys.set(keyNum, stolenKey);
        node.keys.add(fromParent);
        node.pointers.add(stolenChild);
        node.size++;
        stolenChild.parent = node;

        replacePropagate(node.parent, deleting, stolenKey);
    }

    private void stealFromPrevious(LeafNode node, K deleting){
        Pair toMove = node.previous.removeAt(node.previous.pairs.size()-1);
        node.addEntry(toMove);
        replacePropagate(node.parent, deleting, toMove.key);
    }

    private void stealFromPrevious(InternalNode node, K deleting){
        InternalNode previous = node.previous();
        if (previous == null)
            throw new IllegalArgumentException("Node has no previous");

        int keyNum = node.parent.childNum(node)-1;
        K fromParent = node.parent.keys.get(keyNum);

        K stolenKey = previous.keys.remove(previous.size-1);
        Node stolenChild = previous.pointers.remove(previous.size-1);
        previous.size--;

        node.parent.keys.set(keyNum, stolenKey);
        node.keys.add(0, fromParent);
        node.pointers.add(0, stolenChild);
        node.size++;
        stolenChild.parent = node;

        replacePropagate(node.parent, deleting, stolenKey);
    }

    private void replacePropagate(InternalNode node, K oldKey, K newKey){
        int index = Collections.binarySearch(node.keys, oldKey);
        if (index >= 0)
            node.keys.set(index, newKey);
        if (node.parent != null)
            replacePropagate(node.parent, oldKey, newKey);
    }

    private void merge(LeafNode smaller, LeafNode bigger, K leafKey, K inorderSuccessor){
        smaller.addEntries(smaller.pairs.size(), bigger.pairs);
        K deleted = bigger.parent.removeChild(bigger);
        smaller.next = bigger.next;
        if (smaller.hasNext())
            smaller.next.previous = smaller;

        // fixing parent
        InternalNode parent = smaller.parent;
        replacePropagate(smaller.parent, leafKey, inorderSuccessor);
        fixUnderfeeding(parent, deleted);
    }

    private void merge(InternalNode smaller, InternalNode bigger){

        int keyIndex = bigger.parent.childNum(bigger)-1;
        K fromParent = bigger.parent.removeChild(keyIndex+1);

        smaller.keys.add(fromParent);
        smaller.keys.addAll(bigger.keys);
        for (Node child : bigger.pointers)
            child.parent = smaller;
        smaller.pointers.addAll(bigger.pointers);
        smaller.size += 1+bigger.size;

        // fixing parent
        InternalNode parent = smaller.parent;
        fixUnderfeeding(parent, fromParent);
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
        System.out.print(root.asStringTree());
//        Node c = this.root;
//        while (c.getClass().equals(InternalNode.class)){
//            c = ((InternalNode) c).pointers.get(0);
//        }
//        LeafNode l = (LeafNode) c;
//        while (l != null){
//            System.out.print("| ");
//            for (Pair p : l.pairs) {
//                System.out.print(p.key + ",");
//            }
//            System.out.print(" |");
//            l = l.next;
//        }
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

    private abstract class Node {
        InternalNode parent;
        int size;

        public boolean isFull(){
            return this.size >= maxChildren-1;
        }
        public boolean isOverfed(){
            return this.size+1 > maxChildren;
        }
        public boolean isUnderfed(){
            if (this.equals(root))
                return this.size == 0;
            return this.size < (maxChildren-1)/2;
        }
        public boolean hasSpareEntries(){
            return this.size > (maxChildren-1)/2;
        }

        abstract public Node next();
        abstract public Node previous();
        abstract public String asStringTree();
        abstract public void printTreeToBuffer(StringBuilder buffer, String prefix, String childrenPrefix);

        public boolean hasNext(){ return this.next() != null; }
        public boolean hasPrevious(){ return this.previous() != null; }
    }

    private class InternalNode extends Node {
        List<K> keys;
        List<Node> pointers;

        public InternalNode(List<K> keys, List<Node> pointers, InternalNode parent) {
            if (keys.size() != pointers.size()-1)
                throw new IllegalArgumentException("Number of keys does not match with the number of pointers");
            this.keys = keys;
            this.size = keys.size();
            this.pointers = pointers;
            this.parent = parent;
        }

        public int childNum(Node node){
            return this.pointers.indexOf(node);
        }

        public K removeChild(Node node){
            return removeChild(this.pointers.indexOf(node));
        }

        public K removeChild(int pos){
            this.size--;
            this.pointers.remove(pos);
            if (pos > 0)
                return this.keys.remove(pos-1);
            else
                return this.keys.remove(0);
        }

        public InternalNode next(){
            if (this.parent == null)
                return null;

            int index = this.parent.pointers.indexOf(this);
            if (index+1 == this.parent.pointers.size())
                return null;
            return (InternalNode) this.parent.pointers.get(index+1);
        }

        public InternalNode previous(){
            if (this.parent == null)
                return null;

            int index = this.parent.pointers.indexOf(this);
            if (index-1 < 0)
                return null;
            return (InternalNode) this.parent.pointers.get(index-1);
        }


        @Override
        public String toString() {
            return "InternalNode{" +
                    "" + keys +
                    '}';
        }

        public String asStringTree() {
            StringBuilder buffer = new StringBuilder(50);
            printTreeToBuffer(buffer, "", "");
            return buffer.toString();
        }

        public void printTreeToBuffer(StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            for (K key : this.keys)
                buffer.append(key.toString()).append(", ");
            buffer.append('\n');
            for (Iterator<Node> it = this.pointers.iterator(); it.hasNext();) {
                Node next = it.next();
                if (it.hasNext()) {
                    next.printTreeToBuffer(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
                } else {
                    next.printTreeToBuffer(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
                }
            }
        }

    }

    private class LeafNode extends Node{
        List<Pair> pairs;
        LeafNode previous, next;

        public LeafNode(List<Pair> pairs, InternalNode parent, LeafNode previous, LeafNode next) {
            this.pairs = pairs;
            this.size = pairs.size();
            this.previous = previous;
            this.next = next;
            this.parent = parent;
        }

        public void addEntry(K key, V value){
            insertSorted(this.pairs, key, value);
            this.size++;
        }

        public void addEntry(Pair pair){
            insertSorted(this.pairs, pair);
            this.size++;
        }

        public void addEntries(int pos, Collection<? extends Pair> pairs){
            this.pairs.addAll(pos, pairs);
            this.size += pairs.size();
        }

        public int keyPos(K key){
            for (int i = 0; i < this.pairs.size(); i++) {
                if (this.pairs.get(i).key.compareTo(key) == 0)
                    return i;
            }
            throw new IllegalArgumentException("Key is not present");
        }

        public K inorderSuccessor(K key){
            int index = this.keyPos(key);
            if (index < this.size-1)
                return this.pairs.get(index+1).key;

            LeafNode next = this.next();
            while (next != null && next.size == 0)
                next = next.next();

            if (next == null)
                return null;

            return next.pairs.get(0).key;
        }

        public int removeEntry(K key){
            this.size--;
            int index = this.keyPos(key);
            this.pairs.remove(index);
            return index;
        }

        public Pair removeAt(int index){
            this.size--;
            return this.pairs.remove(index);
        }

        @Override
        public String toString() {
            return "LeafNode{" +
                     pairs +
                    '}';
        }

        @Override
        public LeafNode next() {
            return this.next;
        }

        @Override
        public LeafNode previous() {
            return this.previous;
        }

        public String asStringTree() {
            StringBuilder buffer = new StringBuilder(50);
            printTreeToBuffer(buffer, "", "");
            return buffer.toString();
        }

        public void printTreeToBuffer(StringBuilder buffer, String prefix, String childrenPrefix) {
            buffer.append(prefix);
            for (Pair pair : this.pairs)
                buffer.append("<").append(pair.key).append("-").append(pair.value).append(">, ");
            buffer.append('\n');
        }

    }

}











