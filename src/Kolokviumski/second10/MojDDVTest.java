package Kolokviumski.second10;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class AmountNotAllowedException extends Exception {
    public AmountNotAllowedException(int message) {
        super("Receipt with amount " + message + " is not allowed to be scanned\n");
    }
}

enum DDV_Tip {
    A, B, V
}

class Item {
    private int price;
    private DDV_Tip tip;

    public Item(int price, DDV_Tip tip) {
        this.price = price;
        this.tip = tip;
    }

    public Item() {
    }

    public int getPrice() {
        return price;
    }

    public DDV_Tip getTip() {
        return tip;
    }

    public double getTaxReturn() {
        switch (tip) {
            case A:
                return price * 0.18 * 0.15;
            case B:
                return price * 0.05 * 0.15;
            default:
                return 0;
        }
    }

    public void setTip(DDV_Tip tip) {
        this.tip = tip;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}


class Record {
    String id;
    List<Item> items;

    public Record(String id, List<Item> items) throws AmountNotAllowedException {
        this.id = id;
        int sum = items.stream().mapToInt(i -> i.getPrice()).sum();
        if (sum > 30000) {
            throw new AmountNotAllowedException(sum);
        }
        this.items = items;
    }

    public static Record create(String input) throws AmountNotAllowedException {
        String[] parts = input.split("\\s+");

        String id = parts[0];
        List<Item> items = new ArrayList<>();

        Item item = new Item();

        for (int i = 1; i < parts.length; i++) {
            if (i % 2 == 0) {
                item.setTip(DDV_Tip.valueOf(parts[i]));
                items.add(item);
                item = new Item();
            } else {
                item.setPrice(Integer.parseInt(parts[i]));
            }
        }
        return new Record(id, items);
    }

    public double getTaxReturn() {
        return items.stream().mapToDouble(Item::getTaxReturn).sum();
    }

    public String getId() {
        return id;
    }

    public int getSumm() {
        return items.stream().mapToInt(Item::getPrice).sum();
    }

    @Override
    public String toString() {
        return String.format("%10s\t%10d\t%10.5f", this.id, this.getSumm(), this.getTaxReturn());
    }
}

class MojDDV {
    List<Record> records;

    public MojDDV() {
        records = new ArrayList<>();
    }

    public void readRecords(InputStream in) {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        records = br.lines().map(line -> {
                    try {
                        return Record.create(line);
                    } catch (AmountNotAllowedException e) {
                        System.out.print(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void printTaxReturns(PrintStream out) {
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
        records.forEach(record -> pw.println(record.toString()));
        pw.flush();
    }

    //DOPOLNITELNI METODI
    public void printStatistics(OutputStream outputStream) {
        PrintWriter pw = new PrintWriter(outputStream);
        DoubleSummaryStatistics statistics = records.stream()
                .mapToDouble(Record::getTaxReturn).summaryStatistics();

        pw.println(String.format("min:\t%05.03f\nmax:\t%05.03f\nsum:\t%05.03f\ncount:\t%-5d\navg:\t%05.03f",
                statistics.getMin(),
                statistics.getMax(),
                statistics.getSum(),
                statistics.getCount(),
                statistics.getAverage()));

        pw.flush();
    }
// Ako treba da se sortiranit po iznos na povrat na DDV
//
//    void printSorted(OutputStream outputStream) {
//        PrintWriter pw = new PrintWriter(outputStream);
//
//        with comparators
//        Comparator<Record> comparator = Comparator.comparingDouble(Record::getTaxReturn)
//                .thenComparing(Record::getSumm)
//                .thenComparing(Record::getId);
//
//        records.sort(comparator);
//
//        records.forEach(r -> pw.println(r.toString()));
//        pw.flush();
//}
}

public class MojDDVTest {

    public static void main(String[] args) {

        MojDDV mojDDV = new MojDDV();

        System.out.println("===READING RECORDS FROM INPUT STREAM===");
        mojDDV.readRecords(System.in);

        System.out.println("===PRINTING TAX RETURNS RECORDS TO OUTPUT STREAM ===");
        mojDDV.printTaxReturns(System.out);

        //DOPOLNITELNI TESTOVI KODOVI
        System.out.println("===PRINTING SUMMARY STATISTICS FOR TAX RETURNS TO OUTPUT STREAM===");
        mojDDV.printStatistics(System.out);


    }
}
