package Kolokviumski.first10.Shapes1;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

class Canvas implements Comparable<Canvas>{
    @Override
    public int compareTo(Canvas o) {
        return Integer.compare(this.totalPerimeter(), o.totalPerimeter());
    }

    private String id;
    private List<Integer> sizes;

    public Canvas(String id, List<Integer> sizes) {
        this.id = id;
        this.sizes = sizes;
    }

    public int totalPerimeter() {
        return sizes.stream().mapToInt(size -> size * 4).sum();
    }

    public int count() {
        return sizes.size();
    }

    public String getId() {
        return id;
    }
}

class ShapesApplication {

    private List<Canvas> canvases;

    public ShapesApplication() {
        canvases = new ArrayList<>();
    }

    public int readCanvases(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        AtomicInteger totalCanvases = new AtomicInteger();

        br.lines()
                .forEach(line -> {
                    String[] parts = line.split(" ");
                    String id = parts[0];
                    List<Integer> sizes = new ArrayList<>();
                    for (int i = 1; i < parts.length; i++) {
                        sizes.add(Integer.parseInt(parts[i]));
                    }

                    Canvas canvas = new Canvas(id, sizes);
                    canvases.add(canvas);
                    totalCanvases.addAndGet(sizes.size());
                });
        return totalCanvases.get();
    }

    public void printLargestCanvasTo(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);

        if (canvases.isEmpty()){
            pw.println("No canvases available.");
            pw.flush();
            return;
        }

        Canvas largestCanvas =
                canvases.stream()
                        .max(Canvas::compareTo)
                        .orElse(null);

        pw.printf("%s %d %d%n", largestCanvas.getId(), largestCanvas.count(), largestCanvas.totalPerimeter());
        pw.flush();
    }
}


public class Shapes1Test {

    public static void main(String[] args) {
        ShapesApplication shapesApplication = new ShapesApplication();

        System.out.println("===READING SQUARES FROM INPUT STREAM===");
        System.out.println(shapesApplication.readCanvases(System.in));
        //364fbe94 24 30 22 33 32 30 37 18 29 27 33 21 27 26
        System.out.println("===PRINTING LARGEST CANVAS TO OUTPUT STREAM===");
        shapesApplication.printLargestCanvasTo(System.out);

    }
}
