package Kolokviumski.third10;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.*;
import java.util.stream.Collectors;

abstract class Employee implements Comparable<Employee> {
    protected String id;
    protected String level;

    public Employee(String id, String level) {
        this.id = id;
        this.level = level;
    }

    public String getId() {
        return id;
    }

    public String getLevel() {
        return level;
    }

    public abstract double getSalary();


    @Override
    public int compareTo(Employee o) {
        int cmp = Double.compare(o.getSalary(), this.getSalary());
        if (cmp != 0) return cmp;
        cmp = this.level.compareTo(o.level);
        if (cmp != 0) return cmp;
        return this.id.compareTo(o.id);
    }

    @Override
    public String toString() {
        return String.format("Employee ID: %s Level: %s Salary: %.2f", id, level, getSalary());
    }
}

class HourlyEmployee extends Employee {
    private double hours;
    private double hourlyRate;

    public HourlyEmployee(String id, String level, double hours, double hourlyRate) {
        super(id, level);
        this.hours = hours;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double getSalary() {
        if (hours <= 40) {
            return hours * hourlyRate;
        } else {
            return 40 * hourlyRate + (hours - 40) * hourlyRate * 1.5;
        }
    }

    @Override
    public String toString() {
        double regularHours = Math.min(40, hours);
        double overtimeHours = Math.max(0, hours - 40);
        return String.format(
                "Employee ID: %s Level: %s Salary: %.2f Regular hours: %.2f Overtime hours: %.2f",
                id, level, getSalary(), regularHours, overtimeHours
        );
    }

}

class FreelanceEmployee extends Employee {
    private double ticketRate;
    private List<Integer> ticketPoints;

    public FreelanceEmployee(String id, String level, double ticketRate, List<Integer> ticketPoints) {
        super(id, level);
        this.ticketRate = ticketRate;
        this.ticketPoints = ticketPoints;
    }

    @Override
    public double getSalary() {
        int total = ticketPoints.stream().mapToInt(Integer::intValue).sum();
        return total * ticketRate;
    }


    @Override
    public String toString() {
        int totalPoints = ticketPoints.stream().mapToInt(Integer::intValue).sum();
        return String.format(
                "Employee ID: %s Level: %s Salary: %.2f Tickets count: %d Tickets points: %d",
                id, level, getSalary(), ticketPoints.size(), totalPoints
        );
    }

}

class PayrollSystem {
    private Map<String, Double> hourlyRateByLevel;
    private Map<String, Double> ticketRateByLevel;
    private List<Employee> employees;

    public PayrollSystem(Map<String, Double> hourlyRateByLevel, Map<String, Double> ticketRateByLevel) {
        this.hourlyRateByLevel = hourlyRateByLevel;
        this.ticketRateByLevel = ticketRateByLevel;
        this.employees = new ArrayList<>();
    }

    public void readEmployees(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line;
        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) continue;

                String[] parts = line.split(";");
                char type = parts[0].charAt(0);
                String id = parts[1];
                String level = parts[2];

                if (type == 'H') {
                    double hours = Double.parseDouble(parts[3]);
                    double hourlyRate = hourlyRateByLevel.get(level);
                    Employee e = new HourlyEmployee(id, level, hours, hourlyRate);
                    employees.add(e);
                } else if (type == 'F') {
                    List<Integer> points = new ArrayList<>();
                    for (int i = 3; i < parts.length; i++) {
                        if (!parts[i].isEmpty()) {
                            points.add(Integer.parseInt(parts[i]));
                        }
                    }
                    double ticketRate = ticketRateByLevel.get(level);
                    Employee e = new FreelanceEmployee(id, level, ticketRate, points);
                    employees.add(e);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Set<Employee>> printEmployeesByLevels(PrintStream out, Set<String> levels) {
        Map<String, Set<Employee>> res = new LinkedHashMap<>();


        List<String> sortedLevels = new ArrayList<>(levels);
        Collections.sort(sortedLevels);

        sortedLevels.forEach(level -> {
            List<Employee> employeesAtLevel = employees.stream().filter(e -> e.getLevel().equals(level)).sorted().collect(Collectors.toList());

            if (!employeesAtLevel.isEmpty()) {
                Set<Employee> employeeSet = new LinkedHashSet<>(employeesAtLevel);
                res.put(level, employeeSet);
            }
        });
        return res;
    }

}

public class PayrollSystemTest {

    public static void main(String[] args) {

        Map<String, Double> hourlyRateByLevel = new LinkedHashMap<>();
        Map<String, Double> ticketRateByLevel = new LinkedHashMap<>();
        for (int i = 1; i <= 10; i++) {
            hourlyRateByLevel.put("level" + i, 10 + i * 2.2);
            ticketRateByLevel.put("level" + i, 5 + i * 2.5);
        }

        PayrollSystem payrollSystem = new PayrollSystem(hourlyRateByLevel, ticketRateByLevel);

        System.out.println("READING OF THE EMPLOYEES DATA");
        payrollSystem.readEmployees(System.in);

        System.out.println("PRINTING EMPLOYEES BY LEVEL");
        Set<String> levels = new LinkedHashSet<>();
        for (int i = 5; i <= 10; i++) {
            levels.add("level" + i);
        }
        Map<String, Set<Employee>> result = payrollSystem.printEmployeesByLevels(System.out, levels);
        result.forEach((level, employees) -> {
            System.out.println("LEVEL: " + level);
            System.out.println("Employees: ");
            employees.forEach(System.out::println);
            System.out.println("------------");
        });


    }
}