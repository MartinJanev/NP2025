package Aud.aud1.calculator;

public class DivisionStategy implements CalculateStrategy {
    @Override
    public double calculate(double a, double b) {
        if (b != 0) {
            return a / b;
        } else {
            System.out.println("Error: Division by zero");
            return a; // or handle it as needed
        }
    }
}
