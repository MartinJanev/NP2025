package Kolokviumski.third10;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class DiscountsTest {
    public static void main(String[] args) {
        Discounts discounts = new Discounts();
        int stores = discounts.readStores(System.in);
        System.out.println("Stores read: " + stores);
        System.out.println("=== By average discount ===");
        discounts.byAverageDiscount().forEach(System.out::println);
        System.out.println("=== By total discount ===");
        discounts.byTotalDiscount().forEach(System.out::println);
    }
}

class Product {
    int price, discountedPrice;

    public Product(int dicountedPrice,int price) {
        this.price = price;
        this.discountedPrice = dicountedPrice;
    }

    public static Product addProduct(String product) {
        String[] p = product.split(":");
        return new Product(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
    }

    public int discount() {
        return (price - discountedPrice) * 100 / price;
    }

    public int absDiscount() {
        return price - discountedPrice;
    }

    @Override
    public String toString() {
        return String.format("%2d%% %d/%d", discount(), discountedPrice, price);
    }
}

class Store {
    String name;
    List<Product> products;

    public Store(String name, List<Product> products) {
        this.name = name;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public static Store createStore(String line) {
        String[] parts = line.split("\\s+");
        return new Store(parts[0], Arrays.stream(parts).skip(1)
                .map(Product::addProduct)
                .collect(Collectors.toList()));
    }

    public double getAverageDiscount() {
        return products.stream()
                .mapToDouble(Product::discount)
                .average().orElse(0);
    }

    public int totalDiscount() {
        return products.stream()
                .mapToInt(Product::absDiscount)
                .sum();
    }

    @Override
    public String toString() {
        String productStr = products.stream()
                .sorted(Comparator.comparing(Product::discount)
                        .thenComparing(Product::absDiscount).reversed())
                .map(Product::toString)
                .collect(Collectors.joining("\n"));
        double rounded = Math.round(getAverageDiscount() * 10) / 10.;

        return String.format("%s\nAverage discount: %.1f%%\nTotal discount: %d\n%s", name,
                rounded,
                totalDiscount(),
                productStr);

    }
}

class Discounts {
    List<Store> stores;

    public Discounts() {
        stores = new ArrayList<>();
    }

    public int readStores(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        stores = br.lines().map(Store::createStore).collect(Collectors.toList());

        return stores.size();
    }

    public List<Store> byAverageDiscount() {
        return stores.stream()
                .sorted(Comparator.comparingDouble(Store::getAverageDiscount).reversed()
                        .thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }

    public List<Store> byTotalDiscount() {
        return stores.stream()
                .sorted(Comparator.comparingInt(Store::totalDiscount)
                        .thenComparing(Store::getName))
                .limit(3)
                .collect(Collectors.toList());
    }
}