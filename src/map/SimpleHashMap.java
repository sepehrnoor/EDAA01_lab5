package map;

import java.util.Random;

/**
 * Created by Sepehr on 2/12/2017.
 */
public class SimpleHashMap<K, V> implements Map<K, V> {
    private int size;
    private double loadFactor;
    private Entry<K, V>[] table;

    public static void main(String[] args) {
        SimpleHashMap<Integer, Integer> map = new SimpleHashMap<>();
        Random rng = new Random();
        int replaced = 0, iterations = 512, upper = 2048;
        for (int i = 0; i < iterations; i++) {
            int a = rng.nextInt(upper);
            if (map.put(a, a) != null) replaced++;
        }
        System.out.println(map.show());
        System.out.println("Iterations: " + iterations);
        System.out.println("Upper bound for random: " + upper);
        System.out.println("Map elements: " + map.size());
        System.out.println("Entries replaced: " + replaced);
    }

    //default capacity 16
    public SimpleHashMap() {
        this(16);
    }

    public SimpleHashMap(int capacity) {
        table = (Entry<K, V>[]) new Entry[capacity];
        loadFactor = 0.75;
        size = 0;
    }

    public String show() {
        String result = "";
        for (int i = 0; i < table.length; i++) {
            result += i + "\t";
            if (table[i] != null) {
                Entry current = table[i];
                result += current.toString() + "\t";
                while (current.next != null) {
                    result += current.next.toString() + "\t";
                    current = current.next;
                }
            }
            result += "\n";
        }
        return result;
    }

    private int index(K key) {
        int index = key.hashCode() % table.length;
        while (index < 0) index += table.length;
        return index;
    }

    private Entry<K, V> find(int index, K key) {
        if (table[index] == null) return null;
        Entry<K, V> current = table[index];
        while (!current.getKey().equals(key)) {
            if (current.next == null) return null;
            current = current.next;
        }
        return current;
    }


    public V get(Object key) {
        int index = index((K) key);
        if (table[index] == null) return null;
        Entry<K, V> res = find(index, (K) key);
        if (res == null) return null;
        return res.getValue();
    }

    public V put(K key, V value) {
        V result = put2(key, value);
        if (((double) size) / ((double) table.length) > loadFactor) rehash();
        return result;
    }

    private V put2(K key, V value) {
        int index = index(key);
        Entry<K, V> current = find(index, key);
        if (current != null) return current.setValue(value);
        if (table[index] == null) {
            table[index] = new Entry<K, V>(key, value);
            size++;
            return null;
        }
        current = table[index];
        while (current.next != null) {
            current = current.next;
        }
        current.next = new Entry<K, V>(key, value);
        size++;
        return null;
    }

    private void rehash() {
        size = 0;
        Entry<K, V>[] oldTable = table;
        table = (Entry<K, V>[]) new Entry[oldTable.length * 2];
        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null) {
                Entry<K, V> current = oldTable[i];
                put2(current.getKey(), current.getValue());
                while (current.next != null) {
                    current = current.next;
                    put2(current.getKey(), current.getValue());
                }
            }
        }
    }

    public V remove(Object key) {
        int index = index((K) key);

        //if it doesn't exist then don't bother looking for it
        if (find(index, (K) key) == null) return null;

        Entry<K, V> current = table[index];

        //if it's the only element, it must be the one
        if (current.next == null) {
            V value = current.getValue();
            table[index] = null;
            size--;
            return value;
        }

        //first element matches, point array pointer for original element to second element
        if (current.getKey().equals(key)) {
            Entry<K, V> newEntry = current.next;
            V value = current.getValue();
            table[index] = newEntry;
            size--;
            return value;
        }

        //now we know it's not the first element, just iterate till it's found
        while (!current.next.getKey().equals(key)) {
            current = current.next;
        }
        //get the correct value
        V value = current.next.getValue();
        //if the target has a next element set the next pointer of current element to that, otherwise to null. both cases abandon the desired element to garbage collector.
        if (current.next.next != null) current.next = current.next.next;
        else current.next = null;
        size--;
        return value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private static class Entry<K, V> implements Map.Entry<K, V> {
        K key;
        V value;
        Entry<K, V> next;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            next = null;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = getValue();
            this.value = value;
            return oldValue;
        }

        @Override
        public String toString() {
            return key.toString() + "=" + value.toString();
        }
    }
}
