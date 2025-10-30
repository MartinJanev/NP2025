package Aud.aud2;


import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

class Student {
    String id, name;
    int grade, attendance;

    public Student(String id, String name, int grade, int attendance) {
        this.id = id;
        this.name = name;
        this.grade = grade;
        this.attendance = attendance;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", grade=" + grade +
                ", attendance=" + attendance +
                '}';
    }
}

class Course {
    String name;
    Student[] students;
    int size;

    public Course(String name, int capacity) {
        this.name = name;
        students = new Student[capacity];
        this.size = 0;
    }

    public void addStudent(Student s) {
        if (students.length == size) {
            return;
        }

        students[size] = s;
        size++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course: ").append(name).append("\n");
        for (int i = 0; i < size; i++) {
            sb.append(i + 1).append(". ").append(students[i].toString()).append("\n");
        }
        return sb.toString();
    }

    void enroll(Supplier<Student> supplier) {
        addStudent(supplier.get());
    }

    void forEach(Consumer<Student> consumer) {
        for (int i = 0; i < size; i++) {
            consumer.accept(students[i]);
        }
    }

    void conditionalForEach(Predicate<Student> condition, Consumer<Student> consumer) {
        for (int i = 0; i < size; i++) {
            if (condition.test(students[i])) {
                consumer.accept(students[i]);
            }
        }
    }

    int count(Predicate<Student> predicate) {
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (predicate.test(students[i])) {
                count++;
            }
        }
        return count;
    }

    String[] mapToLabels(Function<Student, String> mapper) {
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = mapper.apply(students[i]);
        }
        return result;
    }


}

public class CourseDemo {
    public static void main(String[] args) {
        Course course = new Course("NP", 20);
        Scanner sc = new Scanner(System.in);

        int n = 3;
        Supplier<Student> reader = () -> {
            //id name grade attendance
            String line = sc.nextLine();
            String[] parse = line.split("\\s+");
            return new Student(
                    parse[0],
                    parse[1],
                    Integer.parseInt(parse[2]),
                    Integer.parseInt(parse[3])
            );
        };

        for (int i = 0; i < n; i++) {
            course.enroll(reader);
        }

        System.out.println(course);

        Consumer<Student> printer = s -> System.out.println(s);
        Consumer<Student> attendanceIncreaser = s -> s.attendance += 5;

        course.forEach(printer);
        course.forEach(attendanceIncreaser);
        System.out.println();
        course.forEach(printer);

        Predicate<Student> excellentStudent = s -> s.grade >= 9;
        Predicate<Student> highAttendance = s -> s.attendance >= 80;
        Predicate<Student> veryHighAttendance = s -> s.attendance >= 90;
        Predicate<Student> minPassGrade = s -> s.grade == 6;
        Predicate<Student> lowGradeHighAcceptance = veryHighAttendance.and(minPassGrade);

        System.out.println(course.count(excellentStudent));
        System.out.println(course.count(highAttendance));
        System.out.println(course.count(veryHighAttendance));
        System.out.println(course.count(minPassGrade));
        System.out.println(course.count(lowGradeHighAcceptance));

        course.conditionalForEach(
                lowGradeHighAcceptance,
                s -> s.grade += 1
        );

        System.out.println(course);

        Function<Student, String> mapper = s -> excellentStudent.test(s) ? "Excellent student" : "Regular student";

        for (String s : course.mapToLabels(mapper)) {
            System.out.println(s);
        }
    }
}
