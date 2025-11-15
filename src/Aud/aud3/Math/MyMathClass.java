package Aud.aud3.Math;

import java.util.ArrayList;

public class MyMathClass {

    public static double std(ArrayList<? extends Number> arr) {
        double avg = arr.stream().mapToDouble(Number::doubleValue).average().orElse(0);

        double variance = arr.stream()
                .mapToDouble(
                        num -> Math.pow(avg - num.doubleValue(), 2))
                .average()
                .orElse(0);

        return Math.sqrt(variance);
    }

    public static void main(String[] args) {
        ArrayList<Integer> students = new ArrayList<>();
        students.add(5);
        students.add(10);
        students.add(15);
        students.add(20);
        students.add(60);
        students.add(30);
        System.out.println(String.format("STD: %.2f", MyMathClass.std(students)));
        ArrayList<Double> grades = new ArrayList<>();
        grades.add(5.5);
        grades.add(10.0);
        grades.add(15.5);
        System.out.println(String.format("STD: %.2f", MyMathClass.std(grades)));

    }
}
