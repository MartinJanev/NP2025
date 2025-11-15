package Aud.aud4.F1Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


class F1Race {

    private List<Driver> drivers;

    public void readResults(InputStream in) {
        BufferedReader bf = new BufferedReader(new InputStreamReader(in));

        drivers = bf.lines()
                .filter(Objects::nonNull)
                .map(Driver::createDriver)
                .collect(Collectors.toList());
    }

    public void printSorted(PrintStream out) {
        PrintWriter pw = new PrintWriter(out);
        Collections.sort(drivers);
        int c = 1;
        for (Driver driver : drivers) {
            pw.print(c++);
            pw.println(". " + driver.toString());
        }
        pw.flush();
        pw.close();
    }
}

class Driver implements Comparable<Driver> {
    @Override
    public int compareTo(Driver o) {
        return toMillis(this.fastestLap()) - toMillis(o.fastestLap());
    }

    private String name;
    private List<String> laps;

    public Driver(String name, String lap1, String lap2, String lap3) {
        this.name = name;
        this.laps = new ArrayList<>();
        laps.add(lap1);
        laps.add(lap2);
        laps.add(lap3);
    }

    public String getName() {
        return name;
    }

    public static Driver createDriver(String line) {
        String[] parts = line.split("\\s+");
        return new Driver(parts[0], parts[1], parts[2], parts[3]);
    }

    private static int toMillis(String lap) {
        String[] laps = lap.split(":");
        return Integer.parseInt(laps[0]) * 60000 + Integer.parseInt(laps[1]) * 1000 + Integer.parseInt(laps[2]);
    }

    public String fastestLap() {
        int min = laps.stream()
                .mapToInt(Driver::toMillis)
                .min()
                .getAsInt();

        return laps.stream()
                .filter(l -> toMillis(l) == min)
                .findFirst()
                .get();
    }

    @Override
    public String toString() {
        return String.format("%-10s%10s", name, fastestLap());
    }
}


public class F1Test {

    public static void main(String[] args) {
        F1Race f1Race = new F1Race();
        f1Race.readResults(System.in);
        f1Race.printSorted(System.out);
    }

}
