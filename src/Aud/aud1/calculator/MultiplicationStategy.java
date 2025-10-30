package Aud.aud1.calculator;

public class MultiplicationStategy implements CalculateStrategy{
    @Override
    public double calculate(double a, double b) {
        return a * b;
    }
}
