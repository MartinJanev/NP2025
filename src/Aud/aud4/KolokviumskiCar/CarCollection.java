package Aud.aud4.KolokviumskiCar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class CarByPriceAndPowerComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        if (o1.getPrice() == o2.getPrice()) {
            return Float.compare(o1.getPower(), o2.getPower());
        } else {
            return Integer.compare(o1.getPrice(), o2.getPrice());
        }
    }
}

class RevCarByPriceAndPowerComparator implements Comparator<Car> {

    @Override
    public int compare(Car o2, Car o1) {
        if (o1.getPrice() == o2.getPrice()) {
            return Float.compare(o1.getPower(), o2.getPower());
        } else {
            return Integer.compare(o1.getPrice(), o2.getPrice());
        }
    }
}

class CarModelComparator implements Comparator<Car> {

    @Override
    public int compare(Car o1, Car o2) {
        return o1.getModel().compareTo(o2.getModel());
    }
}

class CarCollection {

    private List<Car> cars;

    public CarCollection() {
        cars = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void sortByPrice(boolean b) {
        if (b) {
            cars.sort(new CarByPriceAndPowerComparator());
        } else {
            cars.sort(new RevCarByPriceAndPowerComparator());
        }
    }

    public List<Car> getList() {
        return cars;
    }

    public List<Car> filterByManufacturer(String manufacturer) {
        List<Car> filtered = new ArrayList<>();

        for (Car car : cars) {
            if (car.getManufacturer().equalsIgnoreCase(manufacturer)) {
                filtered.add(car);
            }
        }
        Collections.sort(filtered, new CarModelComparator());
        return filtered;
    }


}
