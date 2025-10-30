package Aud.aud3.box;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Box<E> {

    private List<E> items;

    public Box() {
        items = new ArrayList<>();
    }

    public void add(E e) {
        items.add(e);
    }

    public E drawItem() {
        Random rand = new Random();
        int index = rand.nextInt(items.size());
        E item = items.get(index);
        items.remove(item);
        return item;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (E item : items) {
            sb.append(item.toString()).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {

        System.out.println("--------- string box ---------");
        Box<String> stringBox = new Box<String>();

        stringBox.add("a");
        stringBox.add("b");
        stringBox.add("c");
        stringBox.add("d");
        stringBox.add("e");
        System.out.println(stringBox);

        System.out.println("--------- integer box ---------");


        Box<Integer> integerBox = new Box<Integer>();
        integerBox.add(1);
        integerBox.add(2);
        integerBox.add(300);

        while (!integerBox.isEmpty()) {
            System.out.println(integerBox.drawItem());
        }


    }
}
