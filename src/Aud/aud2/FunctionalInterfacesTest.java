package Aud.aud2;

import java.util.function.*;

public class FunctionalInterfacesTest {
    public static void main(String[] args) {
        //1. Function
        //for a given string (arg), return the length
        Function<String, Integer> function = (s) -> s.length();
        System.out.println(function.apply("NP"));

        //2. Predicate
        //Boolean function
        //For a given integer, check if it is even

        Predicate<Integer> predicate = a -> a % 2 == 0;
        System.out.println(predicate.test(5));
        System.out.println(predicate.test(6));

        //3. Supplier
        //Give the current time atm
        Supplier<Long> supplier = () -> System.currentTimeMillis();
        System.out.println(supplier.get());

        //4. Consumer
        Consumer<String> consumer = (x) -> System.out.println(x);
    }
}
