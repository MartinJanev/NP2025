package Kolokviumski.last15;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

class Record implements Comparable<Record> {
    private String code, type;
    private int sum;
    private List<Integer> grades;

    public Record(String code, String type) {
        this.code = code;
        this.type = type;
        this.grades = new ArrayList<>();
        sum = 0;
    }

    public String getCode() {
        return code;
    }

    public void addGrade(int grade) {
        sum += grade;
        this.grades.add(grade);
    }

    private static double round(double value, int decimals) {
        return BigDecimal.valueOf(value)
                .setScale(decimals, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double averageRaw() {
        return grades.isEmpty() ? 0.0 : (double) sum / grades.size();
    }

    private double averageRounded(int decimals) {
        return round(averageRaw(), decimals);
    }

    @Override
    public String toString() {
        return String.format("%s %.2f", code, averageRounded(2));
    }


    @Override
    public int compareTo(Record o) {
        return Comparator
                .comparingDouble(Record::averageRaw)
                .reversed()
                .thenComparing(Record::getCode)
                .compare(this, o);
    }


}

class Statistics implements Comparable<Statistics> {
    public String type;
    private int countOfTens;

    public Statistics(String type, int count) {
        this.type = type;
        this.countOfTens = count;
    }

    public String getType() {
        return type;
    }

    public int getCountOfTens() {
        return countOfTens;
    }

    @Override
    public int compareTo(Statistics o) {
        return Comparator
                .comparingInt(Statistics::getCountOfTens)
                .reversed()
                .thenComparing(Statistics::getType)
                .compare(this, o);
    }
}

class StudentRecords {

    Map<String, Set<Record>> records;
    Map<String, Map<Integer, Integer>> grades;

    public StudentRecords() {
        records = new TreeMap<>();
        grades = new HashMap<>();
    }

    public int readRecords(InputStream inputStream) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            return br.lines()
                    .filter(l -> !l.trim().isEmpty())
                    .mapToInt(line -> {
                        String[] parts = line.split("\\s+");
                        String code = parts[0];
                        String type = parts[1];
                        Record record = new Record(code, type);

                        Arrays.stream(parts)
                                .skip(2)
                                .mapToInt(Integer::parseInt)
                                .forEach(grade -> {
                                    grades.computeIfAbsent(type, t -> new TreeMap<>())
                                            .merge(grade, 1, Integer::sum);

                                    record.addGrade(grade);
                                });
                        records.computeIfAbsent(type, t -> new TreeSet<>())
                                .add(record);
                        return 1;
                    })
                    .sum();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeTable(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        records.forEach((type, recordSet) -> {
            pw.println(type);
            recordSet.forEach(pw::println);
        });
        pw.flush();
    }

    public void writeDistribution(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);

        records.keySet().stream()
                .map(type -> new Statistics(
                        type,
                        grades
                                .getOrDefault(type, Collections.emptyMap())
                                .getOrDefault(10, 0)
                )).sorted()
                .forEach(stat -> {
                    String type = stat.getType();
                    pw.println(type);

                    Map<Integer, Integer> perGrade =
                            grades.getOrDefault(type, Collections.emptyMap());

                    perGrade.entrySet().forEach(entry -> {
                        int grade = entry.getKey();
                        int count = entry.getValue();

                        pw.printf("%2d | ", grade);
                        for (int i = 0; i < count; i += 10) {
                            pw.print("*");
                        }
                        pw.printf("(%d)%n", count);
                    });
                });
        pw.flush();
    }
}

public class StudentRecordsTest {
    public static void main(String[] args) {
        System.out.println("=== READING RECORDS ===");
        StudentRecords studentRecords = new StudentRecords();
        int total = studentRecords.readRecords(System.in);
        System.out.printf("Total records: %d\n", total);
        System.out.println("=== WRITING TABLE ===");
        studentRecords.writeTable(System.out);
        System.out.println("=== WRITING DISTRIBUTION ===");
        studentRecords.writeDistribution(System.out);
    }
}

// your code here