
// Decorator Pattern Example

package Aud.aud10;

import java.util.ArrayList;
import java.util.List;

class Order {
    List<String> additions;
    String base;

    public Order(String base) {
        this.additions = new ArrayList<>();
        this.base = base;
    }

    public void addAddition(String addition) {
        additions.add(addition);
    }

    double price() {
        double startPrice = 0.0;
        switch (base) {
            case "ESPRESSO":
                startPrice = 1.2;
                break;
            case "DECAF ESPRESSO":
                startPrice = 1.25;
                break;
            case "BREWED COFFEE":
                startPrice = 1.0;
                break;
        }

        for (String addition : additions) {
            switch (addition) {
                case "ALMOND MILK":
                    startPrice += 1.5;
                    break;
                case "REGULAR MILK", "PUMPKIN SPICE":
                    startPrice += 1.0;
                    break;
            }
        }
        return startPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "base='" + base + '\'' +
                ", additions=" + additions +
                '}';
    }
}

public class CoffeeShopSimple {
    public static void main(String[] args) {
        Order order = new Order("ESPRESSO");
        order.addAddition("ALMOND MILK");
        order.addAddition("PUMPKIN SPICE");

        System.out.println(order.price());
        System.out.println(order);
    }
}
