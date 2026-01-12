package Aud.aud5;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//101 salad pizza salad soup

class Person implements Comparable<Person> {


    private long id;
    private List<String> meals;
    private int heMeals;

    public Person(long id, List<String> meals) {
        this.id = id;
        this.meals = meals;
        this.heMeals = 0;
    }

    public long getId() {
        return id;
    }

    public Person countHealthyMeals(List<String> healthyMeals) {
//        return ((int) meals.stream().filter(healthyMeals::contains).count());
        heMeals = ((int) meals.stream().filter(healthyMeals::contains).distinct().count());
        return this;
    }

    public List<String> getMeals() {
        return meals;
    }

    public static Person create(String line) {
        String[] fields = line.split("\\s+");
        long id = Long.parseLong(fields[0]);
        List<String> meals = new ArrayList<>();

//        for (int i = 1; i < fields.length; i++) {
//            meals.add(fields[i]);
//        }

//        meals = Arrays.stream(fields).skip(1).collect(Collectors.toList());

        IntStream.range(1, fields.length)
                .forEach(i -> meals.add(fields[i]));

        return new Person(id, meals);
    }

    public int getHeMeals() {
        return heMeals;
    }

    public void setHeMeals(int heMeals) {
        this.heMeals = heMeals;
    }

    @Override
    public String toString() {
        return String.format("Person ID: %d (healthy meals: %d)", id, heMeals);
    }

    @Override
    public int compareTo(Person o) {
        return Comparator.comparing(Person::getHeMeals)
                .reversed()
                .thenComparing(Person::getId)
                .compare(this, o);
    }
}


public class HealthyMeals {

    private List<String> healthyMeals;

    public HealthyMeals() {
        healthyMeals = new ArrayList<>();
    }


//    public void evaluate1(InputStream in, OutputStream os) throws IOException {
//        PrintWriter pw = new PrintWriter(os);
//        BufferedReader br = new BufferedReader(new InputStreamReader(in));
//        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).collect(Collectors.toList());
//        br.lines()
//                .map(Person::create)
//                .sorted(Comparator.comparing(p->p.countHealthyMeals(healthyMeals)).thenComparing())
//
//        pw.flush();
//    }

    public void evaluate(InputStream in, OutputStream os) throws IOException {
        PrintWriter pw = new PrintWriter(os);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        healthyMeals = Arrays.stream(br.readLine().split("\\s+")).collect(Collectors.toList());
        br.lines()
                .map(Person::create)
                .map(p -> p.countHealthyMeals(healthyMeals))
                .sorted().forEach(pw::println);

        pw.flush();
    }


    public static void main(String[] args) {
        HealthyMeals healthyMeals = new HealthyMeals();
        try {
            healthyMeals.evaluate(System.in, System.out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
