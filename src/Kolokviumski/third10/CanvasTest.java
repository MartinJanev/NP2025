/*
Да се напише класа Canvas во која што ќе се чуваат форми од различен тип. За секоја форма треба да може да се добијат информации за колкава плоштина и периметар има, како и да се овозможи формата да биде скалирана за некој коефициент. Во класата Canvas да се имплементираат:

default конструктор
void readShapes (InputStream is) - метод за вчитување на информации за формите од влезен поток.
Информациите за секоја форма се дадени во секој ред. При вчитување на формите прво се вчитува број (1 = круг/2 = квадрат/3 = правоаголник), па потоа се чита ИД-то на корисникот што ја креирал формата, па потоа доколку станува збор за круг/квадрат се вчитува еден децимален број за радиусот/страната на кругот/квадартот, а доколку е правоаголник се вчитуваат два децимални броја за должина и висина на правоаголнкот.
ИД на корисникот мора да биде стринг со должина од 6 знаци, при што не се дозволени специјални знаци (само букви и бројки). Доколку некој ИД не е во ред да се фрли исклучок од тип InvalidIDException при креирањето на формата, а со истиот справете се во рамки на функцијата readShapes, односно неправилно ИД да не повлече прекинување на вчитувањето на формите.
Димензија на форма не смее да биде 0. Во таков случај да се фрли исклучок од тип InvalidDimensionException. Овој исклучок треба да го прекине понатамошното читање на останатите форми.
void scaleShapes (String userID, double coef) - метод што ќе ги скалира сите форми креирани од корисникот userID со коефициентот coef (ќе ги помножи сите димензии на формата со тој коефициент).
void printAllShapes (OutputStream os) - метод што ќе ги испечати формите на излезен поток сортирани според нивната плоштина во растечки редослед
void printByUserId (OutputStream os) - метод што ќе ги испечати формите групирани според корисникот којшто ги креирал, при што корисниците ќе се сортирани според бројот на форми што ги имаат креирано (доколку тој број е ист тогаш според сумата на плоштините на формите). Формите на даден корисник ќе се сортирани според периметарот во опаѓачки редослед.
void statistics (OutputStream os) - метод што ќе испечати статистики за плоштините на сите форми во колекцијата (min, max, average, sum, count).
 */

package Kolokviumski.third10;
import java.io.*;
import java.util.*;

class InvalidDimensionException extends Exception {
    InvalidDimensionException() {
        super("Dimension 0 is not allowed!");
    }
}

class InvalidIDException extends Exception {

    public InvalidIDException(String id) {
        super(String.format("ID %s is not valid", id));
    }
}

interface IShape {
    double getArea();

    double getPerimeter();

    void scale(double coef);
}

class Circle implements IShape {
    double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    public double getArea() {
        return Math.pow(radius, 2) * Math.PI;
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }

    @Override
    public void scale(double coef) {
        radius *= coef;
    }

    @Override
    public String toString() {
        return String.format("Circle -> Radius: %.2f Area: %.2f Perimeter: %.2f", radius, getArea(), getPerimeter());
    }
}

class Square implements IShape {
    double a;

    public Square(double a) {
        this.a = a;
    }

    @Override
    public double getArea() {
        return a * a;
    }

    @Override
    public double getPerimeter() {
        return 4 * a;
    }

    @Override
    public void scale(double coef) {
        a *= coef;
    }

    @Override
    public String toString() {
        return String.format("Square: -> Side: %.2f Area: %.2f Perimeter: %.2f", a, getArea(), getPerimeter());
    }
}

class Rectangle extends Square {

    double b;

    public Rectangle(double a, double b) {
        super(a);
        this.b = b;
    }

    @Override
    public double getArea() {
        return a * b;
    }

    @Override
    public double getPerimeter() {
        return 2 * (a + b);
    }

    @Override
    public void scale(double coef) {
        super.scale(coef);
        b *= coef;
    }

    @Override
    public String toString() {
        return String.format("Rectangle: -> Sides: %.2f, %.2f Area: %.2f Perimeter: %.2f", a, b, getArea(), getPerimeter());
    }
}

class Factory {

    private static boolean checkId(String id) {
        if (id.length() != 6) return false;
        for (char c : id.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }

    public static IShape makeShape(String line) throws InvalidDimensionException {
        String[] parts = line.split("\\s+");
        int type = Integer.parseInt(parts[0]);
        double dimension = Double.parseDouble(parts[2]);
        if (dimension == 0) throw new InvalidDimensionException();
        if (type == 1) return new Circle(dimension);
        else if (type == 2) return new Square(dimension);
        else {
            double dimension2 = Double.parseDouble(parts[3]);
            if (dimension2 == 0) throw new InvalidDimensionException();
            return new Rectangle(dimension, dimension2);
        }
    }


    public static String findId(String line) throws InvalidIDException {
        String[] parts = line.split("\\s+");
        String id = parts[1];
        if (!checkId(id)) {
            throw new InvalidIDException(id);
        }
        return id;
    }
}

class Canvas {
    Set<IShape> shapes;
    Map<String, Set<IShape>> shapesByUser;

    public Canvas() {
        shapes = new TreeSet<>(Comparator.comparing(IShape::getArea));
        shapesByUser = new HashMap<>();
    }

    public void readShapes(InputStream in) throws InvalidDimensionException {
        Scanner sc = new Scanner(in);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.isEmpty()) break;
            try {
                String shapeId = Factory.findId(line);
                IShape shape = Factory.makeShape(line);
                shapes.add(shape);
                shapesByUser.putIfAbsent(shapeId, new TreeSet<>(Comparator.comparing(IShape::getPerimeter)));
                shapesByUser.get(shapeId).add(shape);
            } catch (InvalidIDException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void scaleShapes(String userID, double coef) {
        shapesByUser.getOrDefault(userID, new HashSet<>()).forEach(s -> s.scale(coef));
    }

    public void printAllShapes(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        shapes.forEach(pw::println);
        pw.flush();
    }

    public void printByUserId(PrintStream os) {
        PrintWriter pw = new PrintWriter(os);
        Comparator<Map.Entry<String, Set<IShape>>> entry_comparator =
                Comparator.comparing(e -> e.getValue().size());

        shapesByUser.entrySet().stream()
                .sorted(entry_comparator.reversed().thenComparing(
                        e -> e.getValue().stream()
                                .mapToDouble(IShape::getArea).sum()))
                .forEach(e -> {
                    pw.println("Shapes of user: " + e.getKey());
                    e.getValue().forEach(pw::println);
                });
        pw.flush();
    }

    public void statistics(PrintStream os) {
        PrintWriter pw = new PrintWriter(os);
        DoubleSummaryStatistics dss = shapes.stream().mapToDouble(IShape::getArea).summaryStatistics();

        pw.println(String.format("count: %d\nsum: %.2f\nmin: %.2f\naverage: %.2f\nmax: %.2f",
                dss.getCount(), dss.getSum(), dss.getMin(), dss.getAverage(), dss.getMax()));
        pw.flush();
    }
}


public class CanvasTest {

    public static void main(String[] args) {
        Canvas canvas = new Canvas();

        System.out.println("READ SHAPES AND EXCEPTIONS TESTING");
        try {
            canvas.readShapes(System.in);
        } catch (InvalidDimensionException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("BEFORE SCALING");
        canvas.printAllShapes(System.out);
        canvas.scaleShapes("123456", 1.5);
        System.out.println("AFTER SCALING");
        canvas.printAllShapes(System.out);

        System.out.println("PRINT BY USER ID TESTING");
        canvas.printByUserId(System.out);

        System.out.println("PRINT STATISTICS");
        canvas.statistics(System.out);
    }
}