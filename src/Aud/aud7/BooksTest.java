package Aud.aud7;

import javax.print.DocFlavor;
import java.util.*;
import java.util.stream.Collectors;

class BTPComparator implements Comparator<Book> {
    @Override
    public int compare(Book o1, Book o2) {
        if (o1.getTitle().compareToIgnoreCase(o2.getTitle()) == 0) {
            return Float.compare(o1.getPrice(), o2.getPrice());
        }
        return o1.getTitle().compareToIgnoreCase(o2.getTitle());
    }
}

class BPTComparator implements Comparator<Book> {
    @Override
    public int compare(Book o1, Book o2) {
        if (Float.compare(o1.getPrice(), o2.getPrice()) == 0) {
            return o1.getTitle().compareToIgnoreCase(o2.getTitle());
        }
        return Float.compare(o1.getPrice(), o2.getPrice());
    }
}


class Book {
    private String title, category;
    private float price;


    public Book() {
        this.title = "N/A";
        this.category = "N/A";
        this.price = 0;
    }

    public Book(String title, String category, float price) {
        this.title = title;
        this.category = category;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public float getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) %.2f", title, category, price);
    }
}

class BookCollection {
    private List<Book> books;

    public BookCollection() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void printByCategory(String category) {
        books.stream()
                .filter(b -> b.getCategory().compareToIgnoreCase(category) == 0)
                .sorted(new BTPComparator())
                .forEach(System.out::println);

    }

    public List<Book> getCheapestN(int n) {
        return books.stream()
                .sorted(new BPTComparator())
                .limit(n)
                .collect(Collectors.toList());
    }

    public void printByCategoryWithStreams(String category) {
        books.stream().filter(book -> book.getCategory().equals(category))
                .sorted(Comparator.comparing(Book::getTitle).thenComparing(Book::getPrice))
                .forEach(System.out::println);
    }

    public List<Book> getCheapestNWithStreams(int n) {
        return books.stream()
                .sorted(Comparator.comparing(Book::getPrice).thenComparing(Book::getTitle))
                .limit(n)
                .collect(Collectors.toList());
    }


    public List<String> getBookTitles() {
        return books.stream().map(Book::getTitle).collect(Collectors.toList());
    }

    public List<String> getBookCategoriesDistinct() {
        return books.stream().map(Book::getTitle).distinct().collect(Collectors.toList());
    }

    public Book getBookByTitleSubstring(String substring) {
        return books.stream()
                .filter(b -> b.getTitle().toLowerCase().contains(substring.toLowerCase()))
                .findFirst()
                .orElseGet(Book::new);
    }

    public double averageBookPrice() {
        return books.stream()
                .mapToDouble(Book::getPrice)
                .average()
                .orElse(0.0);
    }

    public DoubleSummaryStatistics bookSummaryStatistics() {
        return books.stream().mapToDouble(Book::getPrice).summaryStatistics();
    }

    public Map<String, List<Book>> booksByCategory() {
        return books.stream()
                .collect(Collectors.groupingBy(
                        Book::getCategory,
                        TreeMap::new,
                        Collectors.toList()
                ));
    }

    public Map<String, Long> countBooksByCategory() {
        return books.stream()
                .collect(Collectors.groupingBy(
                        Book::getCategory,
                        Collectors.counting()
                ));
    }

    public Map<String, Double> averageBookPriceByCategory() {
        return books.stream()
                .collect(Collectors.groupingBy(
                        Book::getCategory,
                        Collectors.averagingDouble(Book::getPrice)
                ));
    }

    public Map<String, Float> cheapestBookPriceByCategory() {
        //1
//        Map<String, Optional<Book>> tmp = books.stream()
//                .collect(Collectors.groupingBy(
//                        Book::getCategory,
//                        Collectors.minBy(Comparator.comparing(Book::getPrice))
//                ));
//
//        return tmp.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey, i -> {
//                            return i.getValue().isPresent() ? i.getValue().get().getPrice() : 0;
//                        }
//                ));

        //2

        return books.stream()
                .collect(Collectors.groupingBy(
                        Book::getCategory,
                        Collectors.collectingAndThen(
                                Collectors.minBy(Comparator.comparing(Book::getPrice)),
                                o -> o.map(Book::getPrice).orElse(0F)
                        )
                ));
    }

    public String concatenateTitlesJoining() {
        return books.stream().map(Book::getTitle).collect(Collectors.joining(", "));
    }

    public String concatenateTitlesReduce1() {
        return books.stream()
                .map(Book::getTitle)
                .reduce(
                        "",
                        (result, current) -> {
                            result += current;
                            return result;
                        }
                );
    }

    public String concatenateTitlesReduce2() {
        return books.stream().map(Book::getTitle).reduce("", String::concat);
    }
}

public class BooksTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        BookCollection booksCollection = new BookCollection();
        Set<String> categories = fillCollection(scanner, booksCollection);
        System.out.println("=== PRINT BY CATEGORY ===");
        for (String category : categories) {
            System.out.println("CATEGORY: " + category);
            booksCollection.printByCategory(category);
        }
        System.out.println("=== TOP N BY PRICE ===");
        print(booksCollection.getCheapestN(n));

        System.out.println("=== ALL TITLES ===");
        booksCollection.getBookTitles().forEach(System.out::println);

        System.out.println("=== DISTINCT CATEGORIES ===");
        booksCollection.getBookCategoriesDistinct().forEach(System.out::println);

        System.out.println("=== SEARCH BY SUBSTRING ===");
        System.out.println(booksCollection.getBookByTitleSubstring("java"));

        System.out.println("=== AVERAGE PRICE ===");
        System.out.println(booksCollection.averageBookPrice());

        System.out.println("=== PRICE STATISTICS ===");
        System.out.println(booksCollection.bookSummaryStatistics());

        System.out.println("=== BOOKS BY CATEGORY ===");
        booksCollection.booksByCategory().forEach((k, v) -> {
            System.out.println(k);
            v.forEach(System.out::println);
        });

        System.out.println("=== COUNT BY CATEGORY ===");
        System.out.println(booksCollection.countBooksByCategory());

        System.out.println("=== AVERAGE PRICE BY CATEGORY ===");
        System.out.println(booksCollection.averageBookPriceByCategory());

        System.out.println("=== CHEAPEST PRICE BY CATEGORY ===");
        System.out.println(booksCollection.cheapestBookPriceByCategory());

        System.out.println("=== CONCAT TITLES (JOINING) ===");
        System.out.println(booksCollection.concatenateTitlesJoining());

        System.out.println("=== CONCAT TITLES (REDUCE 1) ===");
        System.out.println(booksCollection.concatenateTitlesReduce1());

        System.out.println("=== CONCAT TITLES (REDUCE 2) ===");
        System.out.println(booksCollection.concatenateTitlesReduce2());

    }

    static void print(List<Book> books) {
        for (Book book : books) {
            System.out.println(book);
        }
    }

    static TreeSet<String> fillCollection(
            Scanner scanner,
            BookCollection collection
    ) {
        TreeSet<String> categories = new TreeSet<String>();
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] parts = line.split(":");
            Book book = new Book(parts[0], parts[1], Float.parseFloat(parts[2]));
            collection.addBook(book);
            categories.add(parts[1]);
        }
        return categories;
    }
}
