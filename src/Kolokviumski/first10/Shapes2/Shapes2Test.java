package Kolokviumski.first10.Shapes2;


import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


class IrregularCanvasException extends Exception {
    public IrregularCanvasException(String message) {
        super(message);
    }
}

class Canvas {
    private String id;
    private int totalShapes;
    private int totalCircles;
    private int totalSquares;
    private double totalArea;
    private double minArea;
    private double maxArea;

    public Canvas(String id) {
        this.id = id;
        this.totalShapes = 0;
        this.totalCircles = 0;
        this.totalSquares = 0;
        this.totalArea = 0;
        this.minArea = Double.MAX_VALUE;
        this.maxArea = Double.MIN_VALUE;
    }

    public String getId() {
        return id;
    }

    public int getTotalShapes() {
        return totalShapes;
    }

    public int getTotalCircles() {
        return totalCircles;
    }

    public int getTotalSquares() {
        return totalSquares;
    }

    public double getTotalArea() {
        return totalArea;
    }

    public double getMinArea() {
        return totalShapes == 0 ? 0.0 : minArea;
    }

    public double getMaxArea() {
        return totalShapes == 0 ? 0.0 : maxArea;
    }

    public double getAverageArea() {
        return totalShapes == 0 ? 0.0 : totalArea / totalShapes;
    }

    private void updateStats(double area) {
        totalShapes++;
        totalArea += area;
        if (area < minArea) minArea = area;
        if (area > maxArea) maxArea = area;
    }

    public void addSquare(double side) {
        double area = side * side;
        totalCircles++;
        updateStats(area);
    }

    public void addCircle(double radius) {
        double area = Math.PI * radius * radius;
        totalSquares++;
        updateStats(area);
    }
}

class ShapesApplication {
    private double maxArea;
    List<Canvas> canvasList;

    public ShapesApplication(double maxArea) {
        this.maxArea = maxArea;
        this.canvasList = new ArrayList<>();
    }

    public void readCanvases(InputStream in) {
        new BufferedReader(new InputStreamReader(in))
                .lines()
                .map(String::trim)
                .map(line -> line.split("\\s+"))
                .forEach(parts -> {
                    String canvasId = parts[0];
                    try {
                        Canvas canvas = new Canvas(canvasId);
                        for (int i = 1; i < parts.length; i += 2) {
                            String type = parts[i];
                            double size = Double.parseDouble(parts[i + 1]);

                            double area = "S".equals(type) ? size * size : "C".equals(type) ? Math.PI * size * size : -1;
                            if (area == -1) continue;

                            if (area > maxArea) {
                                throw new IrregularCanvasException(String.format("Canvas %s has a shape with area larger than %.2f",
                                        canvasId, maxArea));
                            }

                            if ("S".equals(type)) {
                                canvas.addSquare(size);
                            } else {
                                canvas.addCircle(size);
                            }
                        }
                        canvasList.add(canvas);
                    } catch (IrregularCanvasException e) {
                        System.out.println(e.getMessage());
                    }
                });
    }

    public void printCanvases(PrintStream out) {

        canvasList.stream()
                .sorted(
                        Comparator.comparingDouble(Canvas::getTotalArea)
                                .reversed()
                                .thenComparing(Canvas::getId)
                ).forEach(canvas -> out.printf(
                        "%s %d %d %d %.2f %.2f %.2f%n",
                        canvas.getId(),
                        canvas.getTotalShapes(),
                        canvas.getTotalSquares(),
                        canvas.getTotalCircles(),
                        canvas.getMinArea(),
                        canvas.getMaxArea(),
                        canvas.getAverageArea()
                ));
    }
}


public class Shapes2Test {

    public static void main(String[] args) {

        ShapesApplication shapesApplication = new ShapesApplication(10000);

        System.out.println("===READING CANVASES AND SHAPES FROM INPUT STREAM===");
        shapesApplication.readCanvases(System.in);

        System.out.println("===PRINTING SORTED CANVASES TO OUTPUT STREAM===");
        shapesApplication.printCanvases(System.out);


    }
}