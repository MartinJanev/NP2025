package Aud.aud1.calculator;

import java.util.Scanner;

public class Calculator {

    private double state;

    public Calculator() {
        this.state = 0.0;
    }

    public static void main(String[] args) {

        Calculator calculator = new Calculator();

        Scanner input = new Scanner(System.in);

        while (true) {

            String line = input.nextLine();
            if (line.toLowerCase().startsWith("n")) {
                System.out.println("Final result: " + calculator.state);
                break;
            } else if (line.toLowerCase().startsWith("y")) {
                System.out.println("Current result: " + calculator.state);
                calculator.state = 0.0;
                System.out.println("Result: " + calculator.state);
                continue;
            } else if (line.toLowerCase().startsWith("r")) {
                System.out.println("Final result: " + calculator.state);
                continue;
            } else if (line.toLowerCase().matches("[+\\-*/]\\s*\\d+")) {
                System.out.println("Invalid input format. Please use the format: <operator> <number>");
                continue;
            }

            String[] parts = line.split("\\s+");
            char operator = parts[0].charAt(0);
            double amount = Double.parseDouble(parts[1]);

            CalculateStrategy strategy = null;

            if (operator == '+') {
                strategy = new AdditionStategy();
            } else if (operator == '-') {
                strategy = new SubstractionStategy();
            } else if (operator == '*') {
                strategy = new MultiplicationStategy();
            } else if (operator == '/') {
                strategy = new DivisionStategy();
            } else {
                System.out.println("Unknown operator: " + operator);
            }

            calculator.state = strategy.calculate(calculator.state, amount);
            System.out.println("Result: " + calculator.state);
        }
    }
}
