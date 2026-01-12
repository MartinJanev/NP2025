package IspitnaJuni2023;

import java.util.*;

/*
YOUR CODE HERE
DO NOT MODIFY THE interfaces and classes below!!!
*/

class User {
    private String id, name;
    private float totalSpent = 0;
    private int orderCount = 0;
    private Map<String, Location> addresses;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
        this.addresses = new HashMap<>();
    }

    public Location getAddress(String address) {
        return addresses.get(address);
    }

    public void addAddress(String name, Location location) {
        addresses.put(name, location);
    }

    public void addOrder(float money) {
        totalSpent += money;
        orderCount++;
    }

    public float getTotalSpent() {
        return totalSpent;
    }

    public String getId() {
        return id;
    }

    public float getAvgSpent() {
        return orderCount == 0 ? 0 : totalSpent / orderCount;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s Name: %s Total orders: %d Total amount spent: %.2f Average amount spent: %.2f",
                id, name, orderCount, totalSpent, getAvgSpent());
    }
}

class Restaurant {
    private String id, name;
    private Location location;
    private float totalEarnings;
    private int totalOrders;

    public Restaurant(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.totalEarnings = 0;
        this.totalOrders = 0;
    }

    public Location getLocation() {
        return location;
    }

    public void addOrder(float money) {
        totalEarnings += money;
        totalOrders++;
    }

    public float getAvgOrderCost() {
        return totalOrders == 0 ? 0 : totalEarnings / totalOrders;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s Name: %s Total orders: %d Total amount earned: %.2f Average amount earned: %.2f",
                id, name, totalOrders, totalEarnings, getAvgOrderCost());
    }
}

class DeliveryPerson {
    private String id, name;
    private Location location;
    private float totalEarnings = 0;
    private int totalDeliveries = 0;

    public DeliveryPerson(String id, String name, Location location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void addDelivery(float money) {
        totalEarnings += money;
        totalDeliveries++;
    }

    public String getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    public float getTotalEarnings() {
        return totalEarnings;
    }

    private float getAvgEarnings() {
        return totalDeliveries == 0 ? 0 : totalEarnings / totalDeliveries;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %s Name: %s Total deliveries: %d Total delivery fee: %.2f Average delivery fee: %.2f",
                id, name, totalDeliveries, totalEarnings, getAvgEarnings());
    }
}

class DeliveryApp {
    private String appName;
    Map<String, DeliveryPerson> deliveryPeople;
    Map<String, Restaurant> restaurants;
    Map<String, User> users;

    public DeliveryApp(String appName) {
        this.appName = appName;
        this.restaurants = new HashMap<>();
        this.deliveryPeople = new HashMap<>();
        this.users = new HashMap<>();
    }

    public void addUser(String id, String name) {
        users.put(id, new User(id, name));
    }

    public void registerDeliveryPerson(String id, String name, Location location) {
        deliveryPeople.put(id, new DeliveryPerson(id, name, location));
    }

    public void addRestaurant(String id, String name, Location location) {
        restaurants.put(id, new Restaurant(id, name, location));
    }

    public void addAddress(String id, String name, Location location) {
        if (users.containsKey(id)) {
            users.get(id).addAddress(name, location);
        }
    }

    public void orderFood(String userId, String userAddressName, String restaurantId, float cost) {
        User user = users.get(userId);
        Restaurant restaurant = restaurants.get(restaurantId);
        Location userLocation = user.getAddress(userAddressName);

        DeliveryPerson closest = findClosestPerson(restaurant.getLocation());

        if (closest != null) {
            int distance = restaurant.getLocation().distance(userLocation);
            float payment = 90 + (distance / 10) * 10;

            closest.addDelivery(payment);
            closest.setLocation(userLocation);

            user.addOrder(cost);
            restaurant.addOrder(cost);
        }
    }

    private DeliveryPerson findClosestPerson(Location restaurantLocation) {
        DeliveryPerson closestPerson = null;
        int minDistance = Integer.MAX_VALUE;
        int minDeliveries = Integer.MAX_VALUE;

        for (DeliveryPerson person : deliveryPeople.values()) {
            int distance = person.getLocation().distance(restaurantLocation);
            if (distance < minDistance ||
                    (distance == minDistance && person.getTotalDeliveries() < minDeliveries)) {
                closestPerson = person;
                minDistance = distance;
                minDeliveries = person.getTotalDeliveries();
            }
        }
        return closestPerson;
    }

    public void printUsers() {
        users.values().stream()
                .sorted((u1, u2) -> {
                    int result = Float.compare(u2.getTotalSpent(), u1.getTotalSpent());
                    return result != 0
                            ? result
                            : Integer.compare(Integer.parseInt(u1.getId()),
                            Integer.parseInt(u2.getId()));
                })
                .forEach(System.out::println);
    }

    public void printRestaurants() {
        restaurants.values().stream()
                .sorted((r1, r2) -> {
                    int result = Float.compare(r2.getAvgOrderCost(), r1.getAvgOrderCost());
                    return result != 0 ? result : r1.getId().compareTo(r2.getId());
                })
                .forEach(System.out::println);
    }

    public void printDeliveryPeople() {
        deliveryPeople.values().stream()
                .sorted((d1, d2) -> {
                    int result = Float.compare(d2.getTotalEarnings(), d1.getTotalEarnings());
                    return result != 0 ? result : d1.getId().compareTo(d2.getId());
                })
                .forEach(System.out::println);
    }
}

interface Location {
    int getX();

    int getY();

    default int distance(Location other) {
        int xDiff = Math.abs(getX() - other.getX());
        int yDiff = Math.abs(getY() - other.getY());
        return xDiff + yDiff;
    }
}

class LocationCreator {
    public static Location create(int x, int y) {

        return new Location() {
            @Override
            public int getX() {
                return x;
            }

            @Override
            public int getY() {
                return y;
            }
        };
    }
}

public class DeliveryAppTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String appName = sc.nextLine();
        DeliveryApp app = new DeliveryApp(appName);
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(" ");

            if (parts[0].equals("addUser")) {
                String id = parts[1];
                String name = parts[2];
                app.addUser(id, name);
            } else if (parts[0].equals("registerDeliveryPerson")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.registerDeliveryPerson(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addRestaurant")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addRestaurant(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("addAddress")) {
                String id = parts[1];
                String name = parts[2];
                int x = Integer.parseInt(parts[3]);
                int y = Integer.parseInt(parts[4]);
                app.addAddress(id, name, LocationCreator.create(x, y));
            } else if (parts[0].equals("orderFood")) {
                String userId = parts[1];
                String userAddressName = parts[2];
                String restaurantId = parts[3];
                float cost = Float.parseFloat(parts[4]);
                app.orderFood(userId, userAddressName, restaurantId, cost);
            } else if (parts[0].equals("printUsers")) {
                app.printUsers();
            } else if (parts[0].equals("printRestaurants")) {
                app.printRestaurants();
            } else {
                app.printDeliveryPeople();
            }

        }
    }
}

