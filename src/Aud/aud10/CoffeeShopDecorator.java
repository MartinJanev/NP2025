package Aud.aud10;

import java.util.ArrayList;
import java.util.List;

interface Beverage {//Component participant

    double getPrice();

    String receipt();

}

class Espresso implements Beverage {

    @Override
    public double getPrice() {
        return 1.2;
    }

    @Override
    public String receipt() {
        return "Espresso:\n";
    }

}

class Matcha implements Beverage {

    @Override
    public double getPrice() {
        return 1.8;
    }

    @Override
    public String receipt() {
        return "Matcha:\n";
    }
}

class DecafEspresso implements Beverage {

    @Override
    public double getPrice() {
        return 1.25;
    }

    @Override
    public String receipt() {
        return "Decaf Espresso:\n";
    }
}

abstract class BeverageDecorator implements Beverage {

    Beverage beverage;

    public BeverageDecorator(Beverage beverage) {
        this.beverage = beverage;
    }

}

class AlmondMilkDecorator extends BeverageDecorator {

    public AlmondMilkDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return beverage.getPrice() + 1;
    }

    @Override
    public String receipt() {
        return beverage.receipt() + " - almond milk\n";
    }

}

class RegularMilkDecorator extends BeverageDecorator {

    public RegularMilkDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return beverage.getPrice() + 0.5;
    }

    @Override
    public String receipt() {
        return beverage.receipt() + " - regular milk\n";
    }

}

class PumpkinSpiceDecorator extends BeverageDecorator {

    public PumpkinSpiceDecorator(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return beverage.getPrice() + 0.7;
    }

    @Override
    public String receipt() {
        return beverage.receipt() + " - pumpkin spice\n";
    }

}


public class CoffeeShopDecorator {
    public static void main(String[] args) {
        Beverage beverage = new DecafEspresso();

        beverage = new AlmondMilkDecorator(beverage);
        beverage = new PumpkinSpiceDecorator(beverage);

        System.out.println("Total price: " + beverage.getPrice());
        System.out.println("Receipt:\n" + beverage.receipt());


    }
}
