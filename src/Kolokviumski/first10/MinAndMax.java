package Kolokviumski.first10;

import java.util.Scanner;

class MinMax<T extends Comparable<? super T>> {

    private T max, min;

    private int total, countMin, countMax;

    public MinMax() {
    }

    public T max() {
        return this.max;
    }

    public T min() {
        return this.min;
    }


    public void update(T element) {
        if (element == null) return;

        if (min == null && max == null) {
            min = element;
            max = element;
            countMin = 1;
            countMax = 1;
            total = 1;
            return;
        }

        total++;

        int compToMin = element.compareTo(min);
        if (compToMin < 0) {
            min = element;
            countMin = 1;
        } else if (compToMin == 0) {
            countMin++;
        }

        int compToMax = element.compareTo(max);
        if (compToMax > 0) {
            max = element;
            countMax = 1;
        } else if (compToMax == 0) {
            countMax++;
        }
    }

    @Override
    public String toString() {
        if (min == null && max == null) return "";

        int diff;
        if (min.equals(max)) {
            diff = 0;
        } else {
            diff = total - countMin - countMax;
        }
        return String.format("%s %s %d", min, max, diff);
    }
}

public class MinAndMax {
    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        MinMax<String> strings = new MinMax<String>();
        for (int i = 0; i < n; ++i) {
            String s = scanner.next();
            strings.update(s);
        }
        System.out.println(strings);
        System.out.println();
        MinMax<Integer> ints = new MinMax<Integer>();
        for (int i = 0; i < n; ++i) {
            int x = scanner.nextInt();
            ints.update(x);
        }
        System.out.println(ints);;
    }
}