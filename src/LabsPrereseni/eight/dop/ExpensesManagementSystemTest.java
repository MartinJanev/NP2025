package LabsPrereseni.eight.dop;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

enum Level {
    IC, //individual contributor
    M,  //middle management
    C   //C-Level executives
}

class Employee {
    String name;
    String jobTitle;
    Level level;

    public Employee(String name, String jobTitle, Level level) {
        this.name = name;
        this.jobTitle = jobTitle;
        this.level = level;
    }

    @Override
    public String toString() {
        return String.format(
                "Employee: name=%s, title=%s, level=%s",
                name,
                jobTitle,
                level.toString()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name)
                && Objects.equals(jobTitle, employee.jobTitle)
                && level == employee.level;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, jobTitle, level);
    }
}

class Item {
    String name;
    String category;
    double price;

    public Item(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("%s - %s - %.2f USD", name, category, price);
    }

    public double getPrice() {
        return price;
    }
}

class Receipt {
    String merchant;
    LocalDateTime date;
    List<Item> items;

    public Receipt(String merchant, LocalDateTime date, List<Item> items) {
        this.merchant = merchant;
        this.date = date;
        this.items = items;
    }

    double totalAmount() {
        double sum = 0.0;
        for (int i = 0; i < items.size(); i++) {
            sum += items.get(i).price;
        }
        return sum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            sb.append(items.get(i).toString());
            if (i < items.size() - 1) sb.append("; ");
        }

        return String.format(
                "Receipt: merchant=%s, date=%s, items=%s, total=%.2f USD",
                merchant,
                date,
                sb.toString(),
                totalAmount()
        );
    }
}

class NotSupportedExpenseException extends Exception {
    NotSupportedExpenseException(String message) {
        super(message);
    }
}


abstract class Expense {
    protected Employee employee;
    protected String description;

    protected Expense(Employee employee, String description) {
        this.employee = employee;
        this.description = description;
    }

    abstract double refund();

    abstract boolean overlaps(LocalDateTime date);

    public Employee getEmployee() {
        return employee;
    }
}

class TravelExpense extends Expense {

    double travelAmount;
    LocalDateTime start;
    LocalDateTime end;
    String country;

    public TravelExpense(Employee employee, String description, double travelAmount, LocalDateTime start, LocalDateTime end, String country) {
        super(employee, description);
        this.travelAmount = travelAmount;
        this.start = start;
        this.end = end;
        this.country = country;
    }

    @Override
    double refund() {
        double allowance = DailyExpensesPerCountry.ALLOWANCE.getOrDefault(country, 0.0);
        long days = Duration.between(start, end).toDays();
        return travelAmount + allowance * days;
    }

    @Override
    boolean overlaps(LocalDateTime date) {
        return date.isAfter(start) && date.isBefore(end);
    }

    @Override
    public String toString() {
        return String.format(
                "TravelExpense: employee={%s}, description=%s, baseAmount=%.2f USD, " +
                        "country=%s, start=%s, end=%s, refund=%.2f USD",
                employee.toString(),
                description,
                travelAmount,
                country,
                start,
                end,
                refund()
        );
    }
}

class ReceiptExpense extends Expense {

    Receipt receipt;

    public ReceiptExpense(Employee employee, String description, Receipt receipt) {
        super(employee, description);
        this.receipt = receipt;
    }

    @Override
    double refund() {
        return receipt.items.stream()
                .filter(i -> isAllowed(i.category))
                .mapToDouble(Item::getPrice)
                .sum();
    }

    private boolean isAllowed(String category) {
        if (employee.level == Level.C) return true;

        if (employee.level == Level.M) {
            return Set.of(
                    "food",
                    "non-alcohol beverage",
                    "transport",
                    "alcohol beverage"
            ).contains(category);
        }

        // IC (default)
        return category.equals("food") || category.equals("non-alcohol beverage");
    }

    @Override
    boolean overlaps(LocalDateTime date) {
        return false;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    @Override
    public String toString() {
        return String.format(
                "ReceiptExpense: employee={%s}, description=%s, receiptAmount=%.2f USD, " +
                        "receiptDate=%s, itemsCount=%d, refund=%.2f USD",
                employee.toString(),
                description,
                receipt.totalAmount(),
                receipt.date,
                receipt.items.size(),
                refund()
        );
    }
}

class ExpenseManagementSystem {
    private final List<Expense> expenses = new ArrayList<>();
    private final float maxReceiptAmount;

    public ExpenseManagementSystem(float maxReceiptAmount) {
        this.maxReceiptAmount = maxReceiptAmount;
    }

    public void addReceiptExpense(Employee employee, String reason, Receipt receipt) throws NotSupportedExpenseException {
        if (receipt.totalAmount() > maxReceiptAmount) {
            throw new NotSupportedExpenseException(String.format(
                    "Receipt with amount %.2f exceeds the max allowed amount for receipt expense %.2f",
                    receipt.totalAmount(),
                    maxReceiptAmount
            ));
        }

        boolean overlap = expenses.stream()
                .filter(e -> e instanceof TravelExpense)
                .anyMatch(e ->
                        e.getEmployee().equals(employee)
                                && e.overlaps(receipt.date));

        if (overlap) {
            throw new NotSupportedExpenseException(
                    "You cannot add receipt expense in the same period during an approved travel expense."
            );
        }

        expenses.add(new ReceiptExpense(employee, reason, receipt));
    }

    public void addTravelExpense(Employee employee, String reason, double amount,
                                 LocalDateTime start, LocalDateTime end, String country) throws NotSupportedExpenseException {
        expenses.add(new TravelExpense(employee, reason, amount, start, end, country));
    }

    public void printRefunds() {
        expenses.stream()
                .sorted(Comparator.comparingDouble(Expense::refund).reversed())
                .forEach(System.out::println);
    }

    public Map<Employee, Double> totalRefundsPerEmployee() {
        return expenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getEmployee,
                        Collectors.summingDouble(Expense::refund)
                ));
    }
}

class DailyExpensesPerCountry {
    static Map<String, Double> ALLOWANCE = new HashMap<>();

    static {
        ALLOWANCE.put("US", 50.0);
        ALLOWANCE.put("MK", 10.0);
        ALLOWANCE.put("PT", 30.0);

        ALLOWANCE.put("DE", 45.0);   // Germany
        ALLOWANCE.put("AT", 40.0);   // Austria
        ALLOWANCE.put("CH", 55.0);   // Switzerland
        ALLOWANCE.put("FR", 50.0);   // France
        ALLOWANCE.put("IT", 40.0);   // Italy
        ALLOWANCE.put("ES", 35.0);   // Spain
        ALLOWANCE.put("UK", 50.0);   // United Kingdom
        ALLOWANCE.put("NL", 45.0);   // Netherlands
        ALLOWANCE.put("BE", 45.0);   // Belgium
        ALLOWANCE.put("SE", 50.0);   // Sweden
        ALLOWANCE.put("NO", 55.0);   // Norway
        ALLOWANCE.put("DK", 50.0);   // Denmark
        ALLOWANCE.put("PL", 25.0);   // Poland
        ALLOWANCE.put("CZ", 25.0);   // Czech Republic
        ALLOWANCE.put("SK", 20.0);   // Slovakia
        ALLOWANCE.put("HU", 20.0);   // Hungary
        ALLOWANCE.put("HR", 25.0);   // Croatia
        ALLOWANCE.put("BG", 20.0);   // Bulgaria
        ALLOWANCE.put("RO", 20.0);   // Romania
        ALLOWANCE.put("GR", 30.0);   // Greece
        ALLOWANCE.put("RS", 15.0);   // Serbia
        ALLOWANCE.put("AL", 15.0);   // Albania
        ALLOWANCE.put("TR", 20.0);   // Türkiye

        ALLOWANCE.put("CA", 45.0);   // Canada
        ALLOWANCE.put("MX", 25.0);   // Mexico
        ALLOWANCE.put("BR", 20.0);   // Brazil
        ALLOWANCE.put("AR", 18.0);   // Argentina
        ALLOWANCE.put("CL", 22.0);   // Chile

        ALLOWANCE.put("AU", 50.0);   // Australia
        ALLOWANCE.put("NZ", 40.0);   // New Zealand

        ALLOWANCE.put("JP", 45.0);   // Japan
        ALLOWANCE.put("CN", 30.0);   // China
        ALLOWANCE.put("KR", 35.0);   // South Korea
        ALLOWANCE.put("SG", 50.0);   // Singapore
        ALLOWANCE.put("IN", 20.0);   // India
        ALLOWANCE.put("AE", 45.0);   // UAE (Dubai)
        ALLOWANCE.put("SA", 30.0);   // Saudi Arabia
    }
}


public class ExpensesManagementSystemTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Float maxReceiptAmount = Float.parseFloat(sc.nextLine());

        // Create system with some default max amount
        ExpenseManagementSystem system = new ExpenseManagementSystem(maxReceiptAmount);

        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break;
            if (line.isEmpty()) continue;

            String[] parts = line.split(";");
            String method = parts[0];


            switch (method) {

                case "addReceiptExpense": {
                    // Format:
                    // addReceiptExpense;Name;Job;IC|M|C;description;amount;merchant;datetime;item|cat|price,...
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];

                    String merchant = parts[5];
                    LocalDateTime dt = LocalDateTime.parse(parts[6]);

                    // Items list
                    String itemsRaw = parts[7];
                    List<Item> items = new ArrayList<>();
                    if (!itemsRaw.equalsIgnoreCase("none")) {
                        for (String itemStr : itemsRaw.split(",")) {
                            String[] ip = itemStr.split("\\|");
                            items.add(new Item(ip[0], ip[1], Double.parseDouble(ip[2])));
                        }
                    }

                    Employee e = new Employee(empName, job, lvl);
                    Receipt r = new Receipt(merchant, dt, items);

                    try {
                        system.addReceiptExpense(e, description, r);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "addTravelExpense": {
                    // Format:
                    // addTravelExpense;Name;Job;IC|M|C;description;amount;start;end;country
                    String empName = parts[1];
                    String job = parts[2];
                    Level lvl = Level.valueOf(parts[3]);
                    String description = parts[4];
                    double amount = Double.parseDouble(parts[5]);
                    LocalDateTime start = LocalDateTime.parse(parts[6]);
                    LocalDateTime end = LocalDateTime.parse(parts[7]);
                    String country = parts[8];

                    Employee e = new Employee(empName, job, lvl);

                    try {
                        system.addTravelExpense(e, description, amount, start, end, country);
                    } catch (NotSupportedExpenseException ex) {
                        System.out.println(ex.getMessage());
                    }
                    break;
                }

                case "printRefunds": {
                    system.printRefunds();
                    break;
                }

                case "totalRefundsPerEmployee": {
                    Map<Employee, Double> map = system.totalRefundsPerEmployee();
                    map.forEach((emp, total) ->
                            System.out.printf("%s -> %.2f%n", emp.name, total));
                    break;
                }

                default:
                    System.out.println("Unknown method: " + method);
            }
        }
    }
}

