package ru.mail.polis;
import java.util.Comparator;


//TODO: write code here
public class OpenHashTable<E extends Comparable<E>> implements ISet<E> {

    private final int INITIAL_CAPACITY = 8;
    private final float LOAD_FACTOR = 0.5f;
    private final Node DELETED = new Node(null);

    class Node<E>{
        E value;
        Node(E value) {
            this.value = value;
        }
    }

    private Comparator<E> comparator;
    private Node[] table;
    private int size;

    public OpenHashTable() {
        this(null);
        Node<E>[] table = new Node[INITIAL_CAPACITY];
        this.table = table;
    }

    public OpenHashTable(Comparator<E> comparator) {
        this.comparator = comparator;
        Node<E>[] table = new Node[INITIAL_CAPACITY];
        this.table = table;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean contains(E value) {
        int hash,attempt=0;
        hash=hash(value, attempt);
        while(table[hash]!=null && attempt!=table.length) {
            if(value.equals(table[hash].value)) {
                return true;
            }
            attempt++;
            hash=hash(value, attempt);
        }
        return false;
    }

    @Override
    public boolean add(E value) {
        int hash, attempt=0;
   /*     if(value==null)
            return false;//*/
        do{
            hash = hash(value, attempt);
            if(table[hash]==null || table[hash]==DELETED){
                table[hash]=new Node(value);
                size++;
                resize();
                return true;
            }
            if(table[hash].value==value)
                return false;
            attempt++;
        } while(attempt!=table.length);
        return false;
    }

    @Override
    public boolean remove(E value) {
     /*   if(value==null)
            return false;//*/
        int hash=search(value);
        if(hash != -1) {
            table[hash] = DELETED;
            size--;
            return true;
        }
        return false;
    }

    private int hash(E value, int attemptNum) {
        return (Math.abs(value.hashCode()+ attemptNum * (2 * hash_2(value) + 1))) % table.length;
    }

    private int hash_2(E value){
        int hash=5381;
        int valHash=value.hashCode();
        int c;
        do{
            c=valHash%10;
            valHash=valHash/10;
            hash=((hash<<5)+hash)+c;
        }while(valHash>0);
        return hash;
    }

    private void resize() {
        if (size < table.length * LOAD_FACTOR) {
            return;
        }
        Node<E>[] old = this.table;
        size = 0;
        table = new Node[table.length << 1];
        for (int i = 0; i < old.length; i++) {
            if(old[i]!=null && old[i]!=DELETED)
                add(old[i].value);
        }
    }

    private int search(E value) {
        int hash,attempt=0;
        hash=hash(value, attempt);
        while(table[hash]!=null && attempt!=table.length){
            if(value.equals((table[hash].value)))
                return hash;
            attempt++;
            hash=hash(value, attempt);
        }
        return -1;
    }


    public static void main(String[] args) {
        OpenHashTable<String> test = new OpenHashTable<>();
        test.add("hello");
        test.add("hello");
//        test.remove(null);
        test.add("this");
        test.add("is");
        test.add("hash");
        test.add("table");
        System.out.println(test.size());
        System.out.println(test.contains("is"));
        test.remove("is");
        System.out.println(test.contains("is"));
        test.remove("hello");
        test.remove("this");
        test.remove("hash");
        test.remove("table");
        test.add("hello");
        test.add("this");
        test.add("is");
        test.add("hash");
        test.add("table");


    }

}
