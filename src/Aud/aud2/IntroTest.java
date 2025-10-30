package Aud.aud2;


// So eden method, ova se vika functional interface
interface Operation {
    float execute(int a, int b);
}

interface MessageProvider {
    String getMessage();
}

class Addition implements Operation {

    @Override
    public float execute(int a, int b) {
        return a + b;
    }
}

class TraditionalMessage implements MessageProvider {
    @Override
    public String getMessage() {
        return "Hello from class that implements the interface";
    }
}


public class IntroTest {
    public static void main(String[] args) {
        //1. Traditional
        Operation addition = new Addition();

        //2. anonymous class
        Operation substraction = new Operation() {
            @Override
            public float execute(int a, int b) {
                return a - b;
            }
        };

        //3. Lambda expressions
        Operation multiplication = (a, b) -> a * b;

        int x = 5, y = 7;
        System.out.println(addition.execute(x, y));
        System.out.println(substraction.execute(x, y));
        System.out.println(multiplication.execute(x, y));

        System.out.println("-----");

        MessageProvider traditionalMessageProvider = new TraditionalMessage();

        MessageProvider anonymousMessageProvider = new MessageProvider() {
            @Override
            public String getMessage() {
                return "Hello from anonymous class";
            }
        };

        MessageProvider lambdaMessageProvider = () -> "Hello from Lambda expression";

        System.out.println(traditionalMessageProvider.getMessage());
        System.out.println(anonymousMessageProvider.getMessage());
        System.out.println(lambdaMessageProvider.getMessage());
    }
}
