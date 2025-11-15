package Kolokviumski.third10;

import java.util.*;

class Participant {
    String city, code, name;
    int age;

    public Participant(String city, String code, String name, int age) {
        this.city = city;
        this.code = code;
        this.name = name;
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return String.format("%s %s %d", code, name, age);
    }
}

class Audition {

    Map<String, List<Participant>> participantsByCity;

    public Audition() {
        participantsByCity = new HashMap<>();
    }

    public void addParticpant(String city, String code, String name, int age) {
        participantsByCity.putIfAbsent(city, new ArrayList<>());
        List<Participant> participants = participantsByCity.get(city);
        if (participants.stream().noneMatch(p -> p.getCode().equals(code))) {
            participants.add(new Participant(city, code, name, age));
        }
    }

    public void listByCity(String city) {
        List<Participant> participants = participantsByCity.getOrDefault(city, new ArrayList<>());
        participants.stream()
                .sorted(Comparator.comparing(Participant::getName)
                        .thenComparing(Participant::getAge)
                        .thenComparing(Participant::getCode))
                .forEach(System.out::println);
    }
}

public class AuditionTest {
    public static void main(String[] args) {
        Audition audition = new Audition();
        List<String> cities = new ArrayList<String>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(";");
            if (parts.length > 1) {
                audition.addParticpant(parts[0], parts[1], parts[2],
                        Integer.parseInt(parts[3]));
            } else {
                cities.add(line);
            }
        }
        for (String city : cities) {
            System.out.printf("+++++ %s +++++\n", city);
            audition.listByCity(city);
        }
        scanner.close();
    }
}