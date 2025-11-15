package Kolokviumski.first10.ShapesEnum;

import java.util.*;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;


interface Scalable {
    void scale(float scaleFactor);
}

interface Stackable {
    float weight();
}

enum Color {
    RED, GREEN, BLUE
}

abstract class Shape implements Scalable, Stackable {
    private String id;
    private Color color;

    public Shape(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public Color getColor() {
        return color;
    }

    abstract public float weight();
}

class Circle extends Shape {
    private float radius;

    public Circle(String id, Color color, float radius) {
        super(id, color);
        this.radius = radius;
    }

    @Override
    public void scale(float scaleFactor) {
        radius *= scaleFactor;
    }

    @Override
    public float weight() {
        return (float) (Math.PI * radius * radius);
    }

    @Override
    public String toString() {
        return String.format(
                "C: %-5s%-10s%10.2f%n",
                getId(),
                getColor(),
                weight()
        );
    }
}

class Rectangle extends Shape {
    private float width, height;

    public Rectangle(String id, Color color, float width, float height) {
        super(id, color);
        this.width = width;
        this.height = height;
    }

    @Override
    public void scale(float scaleFactor) {
        width *= scaleFactor;
        height *= scaleFactor;
    }

    @Override
    public float weight() {
        return width * height;
    }

    @Override
    public String toString() {
        return String.format(
                "R: %-5s%-10s%10.2f%n",
                getId(),
                getColor(),
                weight()
        );
    }
}

class Canvas {
    private List<Shape> shapes;

    public Canvas() {
        this.shapes = new ArrayList<>();
    }

    private Comparator<Shape> byWeight =
            Comparator.comparing(Shape::weight).reversed();

    private void insert(Shape shape) {
        int indexToPut = shapes.stream()
                .filter(s -> byWeight.compare(s, shape) > 0)
                .findFirst()
                .map(shapes::indexOf)
                .orElse(shapes.size());
        shapes.add(indexToPut, shape);
    }

    public void add(String id, Color color, float radius) {
        Shape circle = new Circle(id, color, radius);
        insert(circle);
    }

    public void add(String id, Color color, float width, float height) {
        Shape rect = new Rectangle(id, color, width, height);
        insert(rect);
    }

    public void scale(String id, float scaleFactor) {
        Optional<Shape> foundShape = shapes.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();

        if (foundShape.isEmpty()) return;

        Shape newShape = foundShape.get();
        shapes.remove(newShape);

        newShape.scale(scaleFactor);

        insert(newShape);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Shape shape : shapes) {
            sb.append(shape.toString());
        }
        return sb.toString();
    }
}

public class ShapesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Canvas canvas = new Canvas();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(" ");
            int type = Integer.parseInt(parts[0]);
            String id = parts[1];
            if (type == 1) {
                Color color = Color.valueOf(parts[2]);
                float radius = Float.parseFloat(parts[3]);
                canvas.add(id, color, radius);
            } else if (type == 2) {
                Color color = Color.valueOf(parts[2]);
                float width = Float.parseFloat(parts[3]);
                float height = Float.parseFloat(parts[4]);
                canvas.add(id, color, width, height);
            } else if (type == 3) {
                float scaleFactor = Float.parseFloat(parts[2]);
                System.out.println("ORIGNAL:");
                System.out.print(canvas);
                canvas.scale(id, scaleFactor);
                System.out.printf("AFTER SCALING: %s %.2f\n", id, scaleFactor);
                System.out.print(canvas);
            }

        }
    }

}
