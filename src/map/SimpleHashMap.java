package map;

import java.util.Random;

/**
 * Created by Sepehr on 2/12/2017.
 */
public class SimpleHashMap<K, V> implements Map<K, V> {
    int size;
    double loadFactor;
    Entry<K, V>[] table;

    public static void main(String[] args) {
        SimpleHashMap<Integer, Integer> map = new SimpleHashMap<>();
        Random rng = new Random();
        for (int i = 0; i < 512; i++) {
            map.put(rng.nextInt(512), rng.nextInt(512));
        }
        System.out.println(map.show());
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
        Entry current = table[index];
        while (!current.getKey().equals(key)) {
            if (current.next == null) return null;
            current = current.next;
        }
        return (Entry<K, V>) current;
    }


    public V get(Object key) {
        int index = index((K) key);
        if (table[index] == null) return null;
        Entry<K, V> current = table[index];
        while (!current.getKey().equals(key)) {
            if (current.next == null) return null;
            current = current.next;
        }
        return current.getValue();
    }

    public V put(K key, V value) {
        V result = put2(key, value);
        if (((double) table.length) / ((double) size) < loadFactor) rehash();
        return result;
    }

    private V put2(K key, V value) {
        int index = index(key);
        if (table[index] == null) {
            table[index] = new Entry<K, V>(key, value);
            size++;
            return null;
        }
        Entry<K, V> current = table[index];
        while (!current.getKey().equals(key)) {
            if (current.next != null) {
                current = current.next;
            } else {
                current.next = new Entry<K, V>(key, value);
                size++;
                return null;
            }
        }
        return current.setValue(value);
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

        //nothing in bucket
        if (table[index] == null) return null;

        //guaranteed at least one element in bucket
        Entry<K, V> current = table[index];

        //check if it's the only element
        if (current.next == null) {
            //matching, remove and return
            if (current.getKey().equals(key)) {
                V value = current.getValue();
                table[index] = null;
                size--;
                return value;
            }
            //not matching
            return null;
        }

        //now guaranteed to have more than one element

        //check if first element matches
        if (current.getKey().equals(key)) {
            V value = current.getValue();
            table[index] = current.next;
            size--;
            return value;
        }

        //current element does not match
        while (!current.next.getKey().equals(key)) {
            //if last element, return null
            if (current.next.next == null) return null;
            current = current.next;
        }
        V value = current.next.getValue();
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
