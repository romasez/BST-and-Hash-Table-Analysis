import java.io.*;
import java.util.*;

public class Benchmark {
    static final int[] SIZES = {100, 1_000, 10_000};
    static final int RUNS = 5;
    static final int WARMUP = 20;
    static volatile long sink; // prevents dead-code elimination

    interface Store {
        void insert(int k, int v);
        Integer search(int k);
        boolean delete(int k);
        int height(); // -1 if not applicable
    }

    static Store newBst() {
        BST<Integer, Integer> t = new BST<>();
        return new Store() {
            public void insert(int k, int v) { t.insert(k, v); }
            public Integer search(int k) { return t.search(k); }
            public boolean delete(int k) { return t.delete(k); }
            public int height() { return t.height(); }
        };
    }

    static Store newHash() {
        HashTable<Integer, Integer> t = new HashTable<>();
        return new Store() {
            public void insert(int k, int v) { t.insert(k, v); }
            public Integer search(int k) { return t.search(k); }
            public boolean delete(int k) { return t.delete(k); }
            public int height() { return -1; }
        };
    }

    static double[] runOnce(boolean bst, int[] keys, int[] searchKeys, int[] deleteKeys) {
        Store s = bst ? newBst() : newHash();
        long t0 = System.nanoTime();
        for (int k : keys) s.insert(k, k);
        long t1 = System.nanoTime();
        int height = s.height();
        long acc = 0;
        long t2 = System.nanoTime();
        for (int k : searchKeys) { Integer v = s.search(k); if (v != null) acc += v; }
        long t3 = System.nanoTime();
        for (int k : deleteKeys) if (s.delete(k)) acc++;
        long t4 = System.nanoTime();
        sink = acc;
        return new double[]{(t1 - t0) / 1e6, (t3 - t2) / 1e6, (t4 - t3) / 1e6, height};
    }

    static double median(double[] a) {
        double[] c = a.clone();
        Arrays.sort(c);
        return c[c.length / 2];
    }

    static int[] shuffled(int[] src, Random rnd) {
        int[] a = src.clone();
        for (int i = a.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return a;
    }

    static int[] sampleKeys(int n, Random rnd) {
        Set<Integer> set = new HashSet<>();
        while (set.size() < n) set.add(rnd.nextInt(n * 10));
        int[] a = new int[n];
        int i = 0;
        for (int k : set) a[i++] = k;
        return shuffled(a, rnd);
    }

    public static void main(String[] args) throws IOException {
        Random rnd = new Random(42);

        // Warm-up so that the JIT compiler has optimized both structures
        for (int w = 0; w < WARMUP; w++) {
            int[] k = sampleKeys(1000, rnd);
            runOnce(true, k, shuffled(k, rnd), shuffled(k, rnd));
            runOnce(false, k, shuffled(k, rnd), shuffled(k, rnd));
        }

        StringBuilder csv = new StringBuilder("scenario,n,structure,insert_ms,search_ms,delete_ms,bst_height\n");
        for (String scenario : new String[]{"random", "sorted"}) {
            for (int n : SIZES) {
                int[] keys = sampleKeys(n, rnd);
                if (scenario.equals("sorted")) Arrays.sort(keys);
                int[] searchKeys = shuffled(keys, rnd);
                int[] deleteKeys = shuffled(keys, rnd);
                for (boolean bst : new boolean[]{true, false}) {
                    double[] ins = new double[RUNS], sea = new double[RUNS], del = new double[RUNS];
                    int height = -1;
                    for (int r = 0; r < RUNS; r++) {
                        double[] res = runOnce(bst, keys, searchKeys, deleteKeys);
                        ins[r] = res[0]; sea[r] = res[1]; del[r] = res[2];
                        height = (int) res[3];
                    }
                    String line = String.format(Locale.US, "%s,%d,%s,%.3f,%.3f,%.3f,%s",
                            scenario, n, bst ? "BST" : "HashTable",
                            median(ins), median(sea), median(del), bst ? String.valueOf(height) : "");
                    csv.append(line).append('\n');
                    System.out.println(line);
                }
            }
        }
        try (PrintWriter pw = new PrintWriter("results/results.csv")) {
            pw.print(csv);
        }
    }
}
