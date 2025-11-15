package Kolokviumski.second10;

import java.util.*;

class InvalidPositionException extends Exception {
    public InvalidPositionException(int position) {
        super(String.format("Invalid position %d, alredy taken!", position));
    }
}

class Component implements Comparable<Component> {
    private String color;
    private int weight;
    private TreeSet<Component> components;

    public Component(String color, int weight) {
        this.color = color;
        this.weight = weight;
        this.components = new TreeSet<>(COMPARATOR);
    }

    private static final Comparator<Component> COMPARATOR =
            Comparator.comparingInt(Component::getWeight)
                    .thenComparing(Component::getColor);

    public String getColor() {
        return color;
    }

    public int getWeight() {
        return weight;
    }

    public TreeSet<Component> getComponents() {
        return components;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    @Override
    public int compareTo(Component o) {
        return COMPARATOR.compare(this, o);
    }

    public String multiStr(String padding) {
        StringBuilder sb = new StringBuilder();
        sb.append(padding).append(this.toString()).append("\n");
        components.forEach(c -> sb.append(c.multiStr(padding + "---")));
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("%d:%s", weight, color);
    }

    public void changeColors(int weight, String color) {
        if (this.weight < weight) {
            this.color = color;
        }
        components.forEach(c -> c.changeColors(weight, color));
    }
}

class Window {
    String name;
    Map<Integer, Component> components;

    public Window(String name) {
        this.name = name;
        this.components = new TreeMap<>();
    }


    public void addComponent(int position, Component component) throws InvalidPositionException {
        if (components.containsKey(position)) {
            throw new InvalidPositionException(position);
        }
        components.put(position, component);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WINDOW ").append(name).append("\n");

        components.entrySet()
                .forEach(e -> {
                    int pos = e.getKey();
                    Component c = e.getValue();
                    sb.append(pos).append(":");
                    sb.append(c.multiStr(""));
                });
        return sb.toString();
    }

    public void changeColor(int weight, String color) {
        components.values()
                .forEach(c -> c.changeColors(weight, color));
    }

    public void swichComponents(int pos1, int pos2) {
        Component c1 = components.get(pos1);
        Component c2 = components.get(pos2);
        components.put(pos1, c2);
        components.put(pos2, c1);
    }
}


public class ComponentTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        Window window = new Window(name);
        Component prev = null;
        while (true) {
            try {
                int what = scanner.nextInt();
                scanner.nextLine();
                if (what == 0) {
                    int position = scanner.nextInt();
                    window.addComponent(position, prev);
                } else if (what == 1) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev = component;
                } else if (what == 2) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                    prev = component;
                } else if (what == 3) {
                    String color = scanner.nextLine();
                    int weight = scanner.nextInt();
                    Component component = new Component(color, weight);
                    prev.addComponent(component);
                } else if (what == 4) {
                    break;
                }

            } catch (InvalidPositionException e) {
                System.out.println(e.getMessage());
            }
            scanner.nextLine();
        }

        System.out.println("=== ORIGINAL WINDOW ===");
        System.out.println(window);
        int weight = scanner.nextInt();
        scanner.nextLine();
        String color = scanner.nextLine();
        window.changeColor(weight, color);
        System.out.println(String.format("=== CHANGED COLOR (%d, %s) ===", weight, color));
        System.out.println(window);
        int pos1 = scanner.nextInt();
        int pos2 = scanner.nextInt();
        System.out.println(String.format("=== SWITCHED COMPONENTS %d <-> %d ===", pos1, pos2));
        window.swichComponents(pos1, pos2);
        System.out.println(window);
    }
}

// вашиот код овде