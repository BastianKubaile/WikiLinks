package main;

import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;


public class IntQueue{
    private Node first;    // beginning of queue
    private Node last;     // end of queue
    private int n;               // number of elements on queue

    // helper linked list class
    private static class Node {
        private int item;
        private Node next;
    }

    /**
     * Initializes an empty queue.
     */
    public IntQueue() {
        first = null;
        last  = null;
        n = 0;
    }

    /**
     * Returns true if this queue is empty.
     *
     * @return {@code true} if this queue is empty; {@code false} otherwise
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * Returns the number of items in this queue.
     *
     * @return the number of items in this queue
     */
    public int size() {
        return n;
    }

    /**
     * Returns the item least recently added to this queue.
     *
     * @return the item least recently added to this queue
     * @throws NoSuchElementException if this queue is empty
     */
    public int peek() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        return first.item;
    }

    /**
     * Adds the item to this queue.
     *
     * @param  item the item to add
     */
    public void enqueue(int item) {
        Node oldlast = last;
        last = new Node();
        last.item = item;
        last.next = null;
        if (isEmpty()) first = last;
        else           oldlast.next = last;
        n++;
    }

    /**
     * Removes and returns the item on this queue that was least recently added.
     *
     * @return the item on this queue that was least recently added
     * @throws NoSuchElementException if this queue is empty
     */
    public int dequeue() {
        if (isEmpty()) throw new NoSuchElementException("Queue underflow");
        int item = first.item;
        first = first.next;
        n--;
        if (isEmpty()) last = null;   // to avoid loitering
        return item;
    }


    /**
     * Returns an iterator that iterates over the items in this queue in FIFO order.
     *
     * @return an iterator that iterates over the items in this queue in FIFO order
     */
//    public Iterator<Item> iterator()  {
//        return new ListIterator<Item>(first);  
//    }

    // an iterator, doesn't implement remove() since it's optional
//    private class ListIterator<Item> implements Iterator<Item> {
//        private Node<Item> current;
//
//        public ListIterator(Node<Item> first) {
//            current = first;
//        }
//
//        public boolean hasNext()  { return current != null;                     }
//        public void remove()      { throw new UnsupportedOperationException();  }
//
//        public Item next() {
//            if (!hasNext()) throw new NoSuchElementException();
//            Item item = current.item;
//            current = current.next; 
//            return item;
//        }
//    }


    /**
     * Unit tests the {@code Queue} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        Queue<String> queue = new Queue<String>();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            if (!item.equals("-"))
                queue.enqueue(item);
            else if (!queue.isEmpty())
                StdOut.print(queue.dequeue() + " ");
        }
        StdOut.println("(" + queue.size() + " left on queue)");
    }
}
