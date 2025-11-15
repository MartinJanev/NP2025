package Kolokviumski.fourth10;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

interface ILocation {
    double getLongitude();

    double getLatitude();

    LocalDateTime getTimestamp();
}

class UserAlreadyExistException extends Exception {
    public UserAlreadyExistException(String id) {
        super(String.format("User with id %s already exists", id));
    }
}

class User {

    String name, id;
    List<ILocation> locations;
    LocalDateTime moreZabolenVreme;
    boolean idiotBolenSimulator;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
        locations = new ArrayList<>();
        idiotBolenSimulator = false;
    }

    public void setMoreZabolenVreme(LocalDateTime moreZabolenVreme) {
        this.moreZabolenVreme = moreZabolenVreme;
    }

    public void setIdiotBolenSimulator(boolean idiotBolenSimulator) {
        this.idiotBolenSimulator = idiotBolenSimulator;
    }

    public void addLocations(List<ILocation> iLocations) {
        locations.addAll(iLocations);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getMoreZabolenVreme() {
        return moreZabolenVreme != null ? moreZabolenVreme : LocalDateTime.MAX;
    }

    public boolean isIdiotBolenSimulator() {
        return idiotBolenSimulator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return name.equals(user.name) && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, id, locations, moreZabolenVreme, idiotBolenSimulator);
    }

    public String userHidden() {
        return String.format("%s %s***", name, id.substring(0, 4));
    }

    String userComplete() {
        return String.format("%s %s %s", name, id, moreZabolenVreme);
    }
}

class LocationUtils {
    public static double timeBetweenInSeconds(ILocation location1, ILocation location2) {
        return Math.abs(Duration.between(location1.getTimestamp(), location2.getTimestamp()).getSeconds());
    }

    public static double distance(ILocation l1, ILocation l2) {
        return Math.sqrt(
                Math.pow(l1.getLatitude() - l2.getLatitude(), 2)
                        + Math.pow(l1.getLongitude() - l2.getLongitude(), 2)
        );
    }

    public static boolean danger(ILocation l1, ILocation l2) {
        return distance(l1, l2) <= 2.0 && timeBetweenInSeconds(l1, l2) <= 300;
    }

    public static int dangerContacts(User u1, User u2) {
        int counter = 0;
        for (ILocation iL1 : u1.locations) {
            for (ILocation iL2 : u2.locations) {
                if (danger(iL1, iL2)) {
                    ++counter;
                }
            }
        }
        return counter;
    }
}

class StopCoronaApp {
    Map<String, User> usersById;
    Map<User, Map<User, Integer>> countingMapForNearContacts;

    public StopCoronaApp() {
        usersById = new HashMap<>();
        countingMapForNearContacts = new TreeMap<>(Comparator.comparing
                (User::getMoreZabolenVreme).thenComparing(us -> us.id));
    }

    public void addUser(String name, String id) throws UserAlreadyExistException {
        if (usersById.containsKey(id)) throw new UserAlreadyExistException(id);
        usersById.put(id, new User(name, id));

    }

    public void addLocations(String id, List<ILocation> locations) {
        usersById.get(id).addLocations(locations);
    }

    public void detectNewCase(String id, LocalDateTime timestamp) {
        User infected = usersById.get(id);
        infected.setIdiotBolenSimulator(true);
        infected.setMoreZabolenVreme(timestamp);
    }

    private Map<User, Integer> getDirectContacts(User u) {
        return countingMapForNearContacts.get(u)
                .entrySet()
                .stream()
                .filter(e -> e.getValue() != 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Collection<User> getIndirectContacts(User u) {
        Set<User> indirect = new TreeSet<>(Comparator.comparing(User::getName)
                .thenComparing(User::getId));
        Map<User, Integer> directContact = getDirectContacts(u);
        directContact.keySet().stream()
                .flatMap(user -> getDirectContacts(u).keySet().stream())
                .filter(user -> !indirect.contains(user) &&
                        !directContact.containsKey(user) && !user.equals(u))
                .forEach(indirect::add);
        return indirect;

    }

    public void createReport() {
        for (User u : usersById.values()) {
            for (User u1 : usersById.values()) {
                if (!u.equals(u1)) {
                    countingMapForNearContacts.putIfAbsent(u, new TreeMap<>(Comparator.comparing(User::getMoreZabolenVreme)
                            .thenComparing(us -> us.id)));
                    countingMapForNearContacts.computeIfPresent(u, (k, v) -> {
                        v.putIfAbsent(u1, 0);
                        v.computeIfPresent(u1, (k1, v1) -> {
                            v1 += LocationUtils.dangerContacts(u, u1);
                            return v1;
                        });
                        return v;
                    });
                }
            }
        }
        List<Integer> directContactsCounts = new ArrayList<>();
        List<Integer> indirectContactsCounts = new ArrayList<>();

        for (User u1 : countingMapForNearContacts.keySet()) {
            if (u1.isIdiotBolenSimulator()) {
                System.out.println(u1.userComplete());
                System.out.println("Direct contacts:");
                Map<User, Integer> directionContact = getDirectContacts(u1);
                directionContact.entrySet().stream()
                        .sorted(comparingByValue(Comparator.reverseOrder()))
                        .forEach(e -> System.out.printf("%s %s%n", e.getKey().userHidden(), e.getValue()));
                int count = directionContact.values().stream().mapToInt(i -> i).sum();
                System.out.printf("Count of direct contacts: %d%n", count);
                directContactsCounts.add(count);

                Collection<User> indirectContacts = getIndirectContacts(u1);
                System.out.println("Indirect contacts: ");
                indirectContacts.forEach(user -> System.out.println(user.userHidden()));
                System.out.printf("Count of indirect contacts: %d%n", indirectContacts.size());
                indirectContactsCounts.add(indirectContacts.size());
            }
        }

        System.out.printf("Average direct contacts: %.4f\n", directContactsCounts.stream().mapToInt(i -> i).average().getAsDouble());
        System.out.printf("Average indirect contacts: %.4f\n", indirectContactsCounts.stream().mapToInt(i -> i).average().getAsDouble());
    }
}

public class StopCoronaTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        StopCoronaApp stopCoronaApp = new StopCoronaApp();

        while (sc.hasNext()) {
            String line = sc.nextLine();
            String[] parts = line.split("\\s+");

            switch (parts[0]) {
                case "REG": //register
                    String name = parts[1];
                    String id = parts[2];
                    try {
                        stopCoronaApp.addUser(name, id);
                    } catch (UserAlreadyExistException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "LOC": //add locations
                    id = parts[1];
                    List<ILocation> locations = new ArrayList<>();
                    for (int i = 2; i < parts.length; i += 3) {
                        locations.add(createLocationObject(parts[i], parts[i + 1], parts[i + 2]));
                    }
                    stopCoronaApp.addLocations(id, locations);

                    break;
                case "DET": //detect new cases
                    id = parts[1];
                    LocalDateTime timestamp = LocalDateTime.parse(parts[2]);
                    stopCoronaApp.detectNewCase(id, timestamp);

                    break;
                case "REP": //print report
                    stopCoronaApp.createReport();
                    break;
                default:
                    break;
            }
        }
    }

    private static ILocation createLocationObject(String lon, String lat, String timestamp) {
        return new ILocation() {
            @Override
            public double getLongitude() {
                return Double.parseDouble(lon);
            }

            @Override
            public double getLatitude() {
                return Double.parseDouble(lat);
            }

            @Override
            public LocalDateTime getTimestamp() {
                return LocalDateTime.parse(timestamp);
            }
        };
    }
}
