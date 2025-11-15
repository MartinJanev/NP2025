package Aud.aud2.demo;

import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Student {
    private final String index;
    private String name;
    private int grade;
    private int attendance;

    public Student(String index, String name, int gpa, int attendance) {
        this.index = index;
        this.name = name;
        this.grade = gpa;
        this.attendance = attendance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        if (grade > 10) {
            grade = 10;
        } else if (grade < 5) {
            grade = 5;
        }
        this.grade = grade;

    }

    public int getAttendance() {
        return attendance;
    }

    @Override
    public String toString() {
        return String.format("%s (%s), grade=%d, attendance=%d%%", name, index, grade, attendance);
    }
}

class Course {
    private final String title;
    private final Student[] students;
    private int size;

    public Course(String title, int capacity) {
        this.title = title;
        this.students = new Student[capacity];
    }

    public int getSize() {
        return size;
    }

    // Enroll using Supplier - Demonstration of Supplier functional interface
    public void enroll(Supplier<Student> supplier) {
        if (size >= students.length) {
            return;
        }
        students[size++] = supplier.get();
    }

    //Apply a Consumer to all students (side effects apply)
    public void forEach(Consumer<Student> action) {
        Arrays.stream(students, 0, size).forEach(action);
    }

    public Student[] filter(Predicate<Student> predicate) {
        int matches = count(predicate);
        return Arrays.stream(students, 0, size)
                .filter(predicate)
                .limit(matches)
                .toArray(Student[]::new);
    }

    private int count(Predicate<Student> predicate) {
        return (int) Arrays.stream(students, 0, size)
                .filter(predicate)
                .count();
    }

    public Student findFirst(Predicate<Student> predicate) {
        return Arrays.stream(students, 0, size)
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }


    public void mutate(Consumer<Student> mutator) {
        forEach(mutator);
    }


    public void conditionalMutate(Predicate<Student> condition, Consumer<Student> mutator) {
        Arrays.stream(students, 0, size)
                .filter(condition)
                .forEach(mutator);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course: " + title + " (" + size + "/" + students.length + " students)");
        Arrays.stream(students).forEach(student -> sb.append(student.toString()).append("\n"));
        return sb.toString();
    }
}


public class CourseDemo2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Course se = new Course("Software Engineering", 10);

        int n = sc.nextInt();

        // Supplier that reads one student per line
        Supplier<Student> studentFromInput = () -> {
            System.out.print("Enter student (index name grade attendance): ");
            String index = sc.next();
            String name = sc.next();
            int grade = sc.nextInt();
            int attendance = sc.nextInt();
            sc.nextLine(); // consume leftover newline
            return new Student(index, name, grade, attendance);
        };

        // Enroll n students using the supplier
        for (int i = 0; i < n; i++) {
            se.enroll(studentFromInput);
        }

        sc.close(); // close scanner after done
        System.out.println("\nEnrolled students:");
        se.forEach(System.out::println);

        // --- Print all enrolled students using Consumer + forEach ---
        System.out.println("\n=== All Students ===");
        Consumer<Student> printer = System.out::println;
        se.forEach(printer);

        // --- Use Predicate to filter passing students ---
        Predicate<Student> isPassing = s -> s.getGrade() >= 6;
        Predicate<Student> goodAttendance = s -> s.getAttendance() >= 70;
        Predicate<Student> passingAndPresent = isPassing.and(goodAttendance);

        System.out.println("\n=== Students with passing grade and good attendance ===");
        Student[] passing = se.filter(passingAndPresent);
        for (Student s : passing) System.out.println(s);

        // --- Find first student with grade >= 9 ---
        System.out.println("\n=== First honor student (grade >= 9) ===");
        Student honor = se.findFirst(s -> s.getGrade() >= 9);
        System.out.println(honor != null ? honor : "None found");

        // --- Mutate: curve all grades by +1 (max 10) ---
        System.out.println("\n=== Curving all grades by +1 (max 10) ===");
        Consumer<Student> curve = s -> s.setGrade(s.getGrade() + 1);
        se.mutate(curve);
        se.forEach(printer);

        // --- Conditional mutation: if attendance is above 90%, award +1 grade to student;
        System.out.println("\n=== Curving high attendance students' grades by +1 ===");

        se.conditionalMutate(
                s -> s.getAttendance() >= 90,
                s -> s.setGrade(s.getGrade() + 1)
        );
    }
}
