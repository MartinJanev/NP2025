package Kolokviumski.fourth10;

import java.util.*;

/**
 * January 2016 Exam problem 2
 */
public class ClusterTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Cluster<Point2D> cluster = new Cluster<>();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            long id = Long.parseLong(parts[0]);
            float x = Float.parseFloat(parts[1]);
            float y = Float.parseFloat(parts[2]);
            cluster.addItem(new Point2D(id, x, y));
        }
        int id = scanner.nextInt();
        int top = scanner.nextInt();
        cluster.near(id, top);
        scanner.close();
    }
}

// your code here

interface Clusterable<T> {
    long id();

    double distance(T item);
}

class Cluster<T extends Clusterable<T>> {
    private Map<Long, T> items;

    public Cluster() {
        items = new HashMap<>();
    }

    public void addItem(T item) {
        items.put(item.id(), item);
    }

    public void near(long id, int top) {
        List<T> list = new ArrayList<>(items.values());
        final T elem = items.get(id);
        list.sort(Comparator.comparingDouble(o -> o.distance(elem)));

        for (int i = 1; i <= top && i < items.size(); i++) {
            T e = list.get(i);
            System.out.printf("%d. %d -> %.3f\n", i, e.id(), e.distance(elem));
        }
    }


}

class Point2D implements Clusterable<Point2D> {

    private long id;
    private float x, y;

    public Point2D(long id, float x, float y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }


    @Override
    public long id() {
        return id;
    }

    @Override
    public double distance(Point2D item) {
        return Math.sqrt((x - item.x) * (x - item.x) + (y - item.y) * (y - item.y));
    }
}
