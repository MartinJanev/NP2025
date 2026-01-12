package Lab.Eight;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum QuestionType {
    TRUEFALSE,
    FREEFORM
}


class TriviaQuestion {

    private final QuestionType type;
    private final String question;        // Actual question
    private final String answer;        // Answer to question
    private final int value;            // Point value of question

    public TriviaQuestion(String question, String answer, int value, QuestionType type) {
        this.question = question;
        this.answer = answer;
        this.value = value;
        this.type = type;
    }

    public QuestionType getType() {
        return type;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public int getValue() {
        return value;
    }

    private static boolean checkCorrectness(String userAnswer, String answer) {
        return !userAnswer.isEmpty()
                && Character.toUpperCase(userAnswer.charAt(0))
                == Character.toUpperCase(answer.charAt(0));
    }

    public boolean isCorrect(String userAnswer) {
        if (type.equals(QuestionType.TRUEFALSE)) {
            return checkCorrectness(userAnswer, answer);
        }
        return userAnswer.equalsIgnoreCase(answer);
    }
}

class TriviaData {

    private final List<TriviaQuestion> questions = new ArrayList<>();

    public void addQuestion(TriviaQuestion question) {
        questions.add(question);
    }

    public TriviaQuestion getQuestion(int index) {
        return questions.get(index);
    }

    public int size() {
        return questions.size();
    }

    public void showQuestion(int index) {
        TriviaQuestion q = questions.get(index);
        System.out.println("Question " + (index + 1) + ".  " + q.getValue() + " points.");
        System.out.println(q.getQuestion());

        if (q.getType().equals(QuestionType.TRUEFALSE)) {
            System.out.println("Enter 'T' for true or 'F' for false.");
        }
    }
}

public class TriviaGame {

    private final TriviaData triviaData = new TriviaData();
    private int score = 0;

    public TriviaGame() {
        loadQuestions();
    }

    private void loadQuestions() {
        triviaData.addQuestion(new TriviaQuestion("The possession of more than two sets of chromosomes is termed?",
                "polyploidy", 3, QuestionType.FREEFORM));
        triviaData.addQuestion(new TriviaQuestion("Erling Kagge skiied into the north pole alone on January 7, 1993.",
                "F", 1, QuestionType.TRUEFALSE));
        triviaData.addQuestion(new TriviaQuestion("1997 British band that produced 'Tub Thumper'",
                "Chumbawumba", 2, QuestionType.FREEFORM));
        triviaData.addQuestion(new TriviaQuestion("I am the geometric figure most like a lost parrot",
                "polygon", 2, QuestionType.FREEFORM));
        triviaData.addQuestion(new TriviaQuestion("Generics were introducted to Java starting at version 5.0.",
                "T", 1, QuestionType.TRUEFALSE));
    }

    public void play() {
        Scanner keyboard = new Scanner(System.in);
        // Ask a question as long as we haven't asked them all


        for (int i = 0; i < triviaData.size(); i++) {
            triviaData.showQuestion(i);
            String userAnswer = keyboard.nextLine();

            TriviaQuestion question = triviaData.getQuestion(i);

            if (question.isCorrect(userAnswer)) {
                score += question.getValue();
                System.out.println("That is correct!  You get " + question.getValue() + " points.");
            } else {
                System.out.println("Wrong, the correct answer is " + question.getAnswer());
            }
            System.out.println("Your score is " + score);


        }
        System.out.println("Game over!  Thanks for playing!");
    }

    public static void main(String[] args) {
        new TriviaGame().play();
    }
}

