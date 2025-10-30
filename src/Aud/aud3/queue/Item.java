package Aud.aud3.queue;


public class Item<E> implements Comparable<Item<E>> {

    private E item;
    private int priority;

    public Item(E item, int priority) {
        this.item = item;
        this.priority = priority;
    }

    public E getItem() {
        return item;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(Item<E> o) {
        return Integer.compare(o.priority, this.priority);
    }


    @Override
    public String toString() {
        return String.format("[%s -- %d]",item,priority);
    }
}