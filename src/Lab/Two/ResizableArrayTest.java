package Lab.Two;

import java.time.LocalTime;
import java.util.Scanner;
import java.util.LinkedList;
import java.util.Timer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;


class ResizableArray<T> {
    private T[] elements;
    private int size;

    @SuppressWarnings("unchecked")
    ResizableArray() {
        elements = (T[]) new Object[1];
        size = 0;
    }

    public void addElement(T element) {
        if (size == elements.length) {
            resize(elements.length * 2);
        }
        elements[size++] = element;
    }

    public boolean removeElement(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {

                for (int j = i; j < size - 1; j++) {
                    elements[j] = elements[j + 1];
                }
                elements[size - 1] = null;
                size--;

                if (size > 0 && size <= elements.length / 4) {
                    resize(Math.max(1, elements.length / 2));
                }
                return true;
            }
        }
        return false;
    }

    public boolean contains(T element) {
        for (int i = 0; i < size; i++) {
            T t = elements[i];
            if ((t == null && element == null) || (t != null && t.equals(element))) {
                return true;
            }
        }
        return false;
    }

    Object[] toArray() {
        Object[] arr = new Object[size];
        for (int i = 0; i < size; i++) {
            arr[i] = elements[i];
        }
        return arr;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int count() {
        return size;
    }

    public T elementAt(int idx) {
        if (idx < 0 || idx >= size) {
            throw new ArrayIndexOutOfBoundsException(idx);
        }
        return elements[idx];
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newArr = (T[]) new Object[newCapacity];
        if (size >= 0) System.arraycopy(elements, 0, newArr, 0, size);
        elements = newArr;
    }

    public static <T> void copyAll(ResizableArray<? super T> dest, ResizableArray<? extends T> src) {
        int n = src.count();
        for (int i = 0; i < n; i++) {
            dest.addElement(src.elementAt(i));
        }
    }

    public static <E> E reduce(ResizableArray<E> source, E identity, BinaryOperator<E> accumulator) {
        E res = identity;

        for (int i = 0; i < source.count(); i++) {
            E element = source.elementAt(i);
            res = accumulator.apply(res, element);
        }

        return res;
    }

    public static <E> ResizableArray<E> copyIf(
            ResizableArray<E> source,
            Predicate<? super E> predicate) {
        ResizableArray<E> res = new ResizableArray<>();

        for (int i = 0; i < source.count(); i++) {
            E element = source.elementAt(i);
            if (predicate.test(element)) {
                res.addElement(element);
            }
        }
        return res;
    }

    public static <E, R> ResizableArray<R> map(
            ResizableArray<E> source,
            Function<? super E, ? extends R> mapper
    ) {
        ResizableArray<R> res = new ResizableArray<>();

        for (int i = 0; i < source.count(); i++) {
            res.addElement(mapper.apply(source.elementAt(i)));
        }

        return res;
    }

}

class IntegerArray extends ResizableArray<Integer> {
    public IntegerArray() {
        super();
    }

    public double sum() {
        double s = 0;
        for (int i = 0; i < this.count(); i++) {
            Integer v = this.elementAt(i);
            if (v != null) {
                s += v;
            }
        }
        return s;
    }

    public double mean() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.sum() / this.count();
    }

    public int countNonZero() {
        int count = 0;
        for (int i = 0; i < this.count(); i++) {
            Integer v = this.elementAt(i);
            if (v != null && v != 0) {
                count++;
            }
        }
        return count;
    }

    public IntegerArray distinct() {
        IntegerArray distinct = new IntegerArray();
        for (int i = 0; i < this.count(); i++) {
            Integer v = this.elementAt(i);
            if (!distinct.contains(v)) {
                distinct.addElement(v);
            }
        }
        return distinct;
    }

    public IntegerArray increment(int offset) {

        IntegerArray incremented = new IntegerArray();

        for (int i = 0; i < this.count(); i++) {
            Integer v = this.elementAt(i);
            if (v != null) {
                incremented.addElement(v + offset);
            } else {
                incremented.addElement(null);
            }
        }

        return incremented;
    }
}

//class ArrayTransformer {
//
//    public static <T, R> ResizableArray<R> map(ResizableArray<T> source, Function<? super T, ? extends R> mapper) {
//        ResizableArray<R> res = new ResizableArray<>();
//
//        for (int i = 0; i < source.count(); i++) {
//            res.addElement(mapper.apply(source.elementAt(i)));
//        }
//
//        return res;
//    }
//
//    public static <T> ResizableArray<T> filter(ResizableArray<T> source, Predicate<? super T> predicate) {
//        ResizableArray<T> res = new ResizableArray<>();
//
//        for (int i = 0; i < source.count(); i++) {
//            T element = source.elementAt(i);
//            if (predicate.test(element)) {
//                res.addElement(element);
//            }
//        }
//
//        return res;
//
//    }
//}

public class ResizableArrayTest {

    public static void main(String[] args) {

        Scanner jin = new Scanner(System.in);
        int test = jin.nextInt();

        if (test == 0) { //test ResizableArray on ints
            ResizableArray<Integer> a = new ResizableArray<Integer>();
            System.out.println(a.count());
            int first = jin.nextInt();
            a.addElement(first);
            System.out.println(a.count());
            int last = first;
            while (jin.hasNextInt()) {
                last = jin.nextInt();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
        }
        if (test == 1) { //test ResizableArray on strings
            ResizableArray<String> a = new ResizableArray<String>();
            System.out.println(a.count());
            String first = jin.next();
            a.addElement(first);
            System.out.println(a.count());
            String last = first;
            for (int i = 0; i < 4; ++i) {
                last = jin.next();
                a.addElement(last);
            }
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(a.removeElement(first));
            System.out.println(a.contains(first));
            System.out.println(a.count());
            ResizableArray<String> b = new ResizableArray<String>();
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));
            System.out.println(b.removeElement(first));
            System.out.println(b.contains(first));

            System.out.println(a.removeElement(first));
            ResizableArray.copyAll(b, a);
            System.out.println(b.count());
            System.out.println(a.count());
            System.out.println(a.contains(first));
            System.out.println(a.contains(last));
            System.out.println(b.contains(first));
            System.out.println(b.contains(last));
        }
        if (test == 2) { //test IntegerArray
            IntegerArray a = new IntegerArray();
            System.out.println(a.isEmpty());
            while (jin.hasNextInt()) {
                a.addElement(jin.nextInt());
            }
            jin.next();
            System.out.println(a.sum());
            System.out.println(a.mean());
            System.out.println(a.countNonZero());
            System.out.println(a.count());
            IntegerArray b = a.distinct();
            System.out.println(b.sum());
            IntegerArray c = a.increment(5);
            System.out.println(c.sum());
            if (a.sum() > 100)
                ResizableArray.copyAll(a, a);
            else
                ResizableArray.copyAll(a, b);
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.removeElement(jin.nextInt()));
            System.out.println(a.sum());
            System.out.println(a.contains(jin.nextInt()));
            System.out.println(a.contains(jin.nextInt()));
        }
        if (test == 3) { //test insanely large arrays
            LinkedList<ResizableArray<Integer>> resizable_arrays = new LinkedList<ResizableArray<Integer>>();
            for (int w = 0; w < 500; ++w) {
                ResizableArray<Integer> a = new ResizableArray<Integer>();
                int k = 2000;
                int t = 1000;
                for (int i = 0; i < k; ++i) {
                    a.addElement(i);
                }

                a.removeElement(0);
                for (int i = 0; i < t; ++i) {
                    a.removeElement(k - i - 1);
                }
                resizable_arrays.add(a);
            }
            System.out.println("You implementation finished in less then 3 seconds, well done!");
        }
    }

}
