public class HashTable<K, V> {

    private static class Entry<K, V> {
        final K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    private Entry<K, V>[] buckets;
    private int size;
    private final double maxLoad;

    public HashTable() {
        this(8, 0.75);
    }

    @SuppressWarnings("unchecked")
    public HashTable(int capacity, double maxLoad) {
        this.buckets = (Entry<K, V>[]) new Entry[capacity];
        this.maxLoad = maxLoad;
    }

    private int index(K key, int capacity) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] old = buckets;
        Entry<K, V>[] fresh = (Entry<K, V>[]) new Entry[old.length * 2];
        for (Entry<K, V> head : old) {
            for (Entry<K, V> e = head; e != null; ) {
                Entry<K, V> next = e.next;
                int i = index(e.key, fresh.length);
                e.next = fresh[i];
                fresh[i] = e;
                e = next;
            }
        }
        buckets = fresh;
    }

    public void insert(K key, V value) {
        int i = index(key, buckets.length);
        for (Entry<K, V> e = buckets[i]; e != null; e = e.next) {
            if (e.key.equals(key)) {
                e.value = value; // update existing key
                return;
            }
        }
        buckets[i] = new Entry<>(key, value, buckets[i]);
        size++;
        if ((double) size / buckets.length > maxLoad) resize();
    }

    public V search(K key) {
        for (Entry<K, V> e = buckets[index(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) return e.value;
        }
        return null;
    }

    public boolean contains(K key) {
        for (Entry<K, V> e = buckets[index(key, buckets.length)]; e != null; e = e.next) {
            if (e.key.equals(key)) return true;
        }
        return false;
    }

    public boolean delete(K key) {
        int i = index(key, buckets.length);
        Entry<K, V> prev = null;
        for (Entry<K, V> e = buckets[i]; e != null; prev = e, e = e.next) {
            if (e.key.equals(key)) {
                if (prev == null) buckets[i] = e.next;
                else prev.next = e.next;
                size--;
                return true;
            }
        }
        return false;
    }

    public int size() {
        return size;
    }
}
