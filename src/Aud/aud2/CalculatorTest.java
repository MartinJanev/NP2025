package Aud.aud2;

import java.util.Scanner;

interface BinaryOperation {
    float execute(float a, float b);
}


class UnknownOperatorException extends Exception {
    public UnknownOperatorException(char operator) {
        super(String.format("%c is unknown operation", operator));
    }
}

class BinaryOperationFactory {
    private double result;
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVIDE = '/';


    public static BinaryOperation get(char operator) throws UnknownOperatorException {
        if (operator == PLUS) {
            return ((a, b) -> a + b);
        } else if (operator == MINUS) {
            return (x, y) -> x - y;
        } else if (operator == MULTIPLY) {
            return (a, b) -> a * b;
        } else if (operator == DIVIDE) {
            return (i, j) -> i / j;
        } else {
            throw new UnknownOperatorException(operator);
        }
    }
}

class Calculator {
    private float result;
    private static final char PLUS = '+';
    private static final char MINUS = '-';
    private static final char MULTIPLY = '*';
    private static final char DIVIDE = '/';

    public Calculator() {
        result = 0;
    }

    public String init() {
        return String.format("result = %f", result);
    }

    public double getResult() {
        return result;
    }

    public String execute(char operator, float value) throws UnknownOperatorException {
        BinaryOperation operation = BinaryOperationFactory.get(operator);
        result = operation.execute(result, value);
        return String.format("result %c %f = %f", operator, value, result);
    }

    @Override
    public String toString() {
        return String.format("updated result = %f", result);
    }
}

public class CalculatorTest {
    static final char RESULT = 'r';

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Calculator calculator = new Calculator();
            System.out.println(calculator.init());

            while (true) {
                String line = scanner.nextLine();
                char choice = getCharLower(line);

                if (choice == RESULT) {
                    System.out.printf("final result = %f%n", calculator.getResult());
                    break;
                }

                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    System.out.println("Please enter: <operator> <number>");
                    continue;
                }

                char operator = parts[0].charAt(0);
                float value = parts[1].charAt(0) - '0';

                try {
                    String result = calculator.execute(operator, value);
                    System.out.println(result);
                    System.out.println(calculator);
                } catch (UnknownOperatorException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("(Y/N)");
            String again = scanner.nextLine();
            char choice2 = getCharLower(again);
            if (choice2 == 'n') {
                break;
            }
        }
    }

    static char getCharLower(String line) {
        if (line.trim().length() > 0) {
            return Character.toLowerCase(line.charAt(0));
        }
        return '?';
    }
}