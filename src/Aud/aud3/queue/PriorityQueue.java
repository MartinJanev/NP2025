package Aud.aud3.queue;

import java.util.Set;
import java.util.TreeSet;


public class PriorityQueue<E extends Drawable> {
    private Set<Item<E>> items;

    public PriorityQueue() {
        items = new TreeSet<>();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void add(E e, int priority) {
        items.add(new Item<>(e, priority));
    }

    public Item<E> delete() {
        Item<E> item = items.iterator().next();
        items.remove(item);

        return item;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item<E> item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        PriorityQueue<DrawingItem> queue = new PriorityQueue<DrawingItem>();

        queue.add(new DrawingItem("asdd"), 12);
        queue.add(new DrawingItem("erf"), 22);
        queue.add(new DrawingItem("dff"), 15);

//        PriorityQueue<String> queue = new PriorityQueue<String>();
//        queue.add("Sting", 12);
//        queue.add("Sting", 13);
//        queue.add("Sting", 14);
//        queue.add("Sting", 15);
//        queue.add("Sting", 16);
//        queue.add("Sting", 17);

        while (!queue.isEmpty()) {
            System.out.println(queue.delete());
        }

    }
}
