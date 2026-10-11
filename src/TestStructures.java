import java.util.*;

public class TestStructures {
    private static int passed = 0;

    private static void check(boolean cond, String name) {
        if (!cond) throw new AssertionError("FAILED: " + name);
        passed++;
    }

    interface Store {
        void insert(int k, int v);
        Integer search(int k);
        boolean delete(int k);
        int size();
    }

    static Store bst() {
        BST<Integer, Integer> t = new BST<>();
        return new Store() {
            public void insert(int k, int v) { t.insert(k, v); }
            public Integer search(int k) { return t.search(k); }
            public boolean delete(int k) { return t.delete(k); }
            public int size() { return t.size(); }
        };
    }

    static Store hash() {
        HashTable<Integer, Integer> t = new HashTable<>();
        return new Store() {
            public void insert(int k, int v) { t.insert(k, v); }
            public Integer search(int k) { return t.search(k); }
            public boolean delete(int k) { return t.delete(k); }
            public int size() { return t.size(); }
        };
    }

    static void runCommon(String label, Store s) {
        for (int i = 0; i < 100; i++) s.insert(i, i * 2);
        check(s.size() == 100, label + ": size after insert");
        check(s.search(42) == 84, label + ": search existing");
        check(s.search(1000) == null, label + ": search missing");

        s.insert(1, -1);
        check(s.size() == 100 && s.search(1) == -1, label + ": update existing key");
    }

    static void runDelete(String label, Store s) {
        Random rnd = new Random(1);
        List<Integer> keys = new ArrayList<>();
        Set<Integer> seen = new HashSet<>();
        while (keys.size() < 500) {
            int k = rnd.nextInt(10000);
            if (seen.add(k)) keys.add(k);
        }
        for (int k : keys) s.insert(k, k);
        Collections.shuffle(keys, rnd);
        for (int i = 0; i < 250; i++) {
            check(s.delete(keys.get(i)), label + ": delete existing");
            check(!s.delete(keys.get(i)), label + ": delete twice");
        }
        for (int i = 0; i < 250; i++) check(s.search(keys.get(i)) == null, label + ": deleted gone");
        for (int i = 250; i < 500; i++) check(s.search(keys.get(i)).equals(keys.get(i)), label + ": others kept");
        check(s.size() == 250, label + ": size after deletes");
    }

    public static void main(String[] args) {
        runCommon("BST", bst());
        runCommon("Hash", hash());
        runDelete("BST", bst());
        runDelete("Hash", hash());

        BST<Integer, Integer> t = new BST<>();
        Random rnd = new Random(2);
        Set<Integer> keys = new TreeSet<>();
        while (keys.size() < 300) keys.add(rnd.nextInt(1000));
        List<Integer> list = new ArrayList<>(keys);
        Collections.shuffle(list, rnd);
        for (int k : list) t.insert(k, k);
        for (int i = 0; i < 100; i++) { t.delete(list.get(i)); keys.remove(list.get(i)); }
        check(t.inorder().equals(new ArrayList<>(keys)), "BST: inorder sorted after deletes");

        System.out.println("All tests passed (" + passed + " checks).");
    }
}
