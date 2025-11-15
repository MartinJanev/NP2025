package Kolokviumski.second10;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Element {
    private final int timeFrom;
    private final int timeTo;
    private final String text;
    private final int number;

    public Element(int number, String time, String text) {
        this(
                number,
                stringToTime(time.split("-->")[0].trim()),
                stringToTime(time.split("-->")[1].trim()),
                text
        );
    }

    public Element(int number, int timeFrom, int timeTo, String text) {
        this.number = number;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.text = text;
    }

    // Pure/functional shift – returns a NEW Element instead of mutating this one
    public Element shifted(int ms) {
        return new Element(number, timeFrom + ms, timeTo + ms, text);
    }

    public boolean containsText(String someText) {
        return text.contains(someText);
    }

    public int getNumber() {
        return number;
    }

    static int stringToTime(String time) {
        String[] parts = time.split(",");
        int res = Integer.parseInt(parts[1]);
        parts = parts[0].split(":");
        int sec = Integer.parseInt(parts[2]);
        int min = Integer.parseInt(parts[1]);
        int h = Integer.parseInt(parts[0]);
        res += sec * 1000;
        res += min * 60 * 1000;
        res += h * 60 * 60 * 1000;
        return res;
    }

    static String timeToString(int time) {
        int h = time / (60 * 60 * 1000);
        time = time % (60 * 60 * 1000);
        int m = time / (60 * 1000);
        time = time % (60 * 1000);
        int s = time / 1000;
        int ms = time % 1000;
        return String.format("%02d:%02d:%02d,%03d", h, m, s, ms);
    }

    @Override
    public String toString() {
        return String.format(
                "%d%n%s --> %s%n%s",
                number,
                timeToString(timeFrom),
                timeToString(timeTo),
                text
        );
    }
}
public class SubtitlesTest {
    public static void main(String[] args) {
        Subtitles subtitles = new Subtitles();
        int n = subtitles.loadSubtitles(System.in);
        System.out.println("+++++ ORIGINIAL SUBTITLES +++++");
        subtitles.print();
        int shift = n * 37;
        shift = (shift % 2 == 1) ? -shift : shift;
        System.out.println(String.format("SHIFT FOR %d ms", shift));
        subtitles.shift(shift);
        System.out.println("+++++ SHIFTED SUBTITLES +++++");
        subtitles.print();
    }

}
class Subtitles {

    private List<Element> elements;

    public Subtitles() {
        elements = new ArrayList<>();
    }

    public int loadSubtitles(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.trim().isEmpty()) {
                continue;
            }
            int number = Integer.parseInt(line);
            String time = scanner.nextLine();
            StringBuilder text = new StringBuilder();
            while (true) {
                if (!scanner.hasNextLine()) break;
                line = scanner.nextLine();
                if (line.trim().isEmpty())
                    break;
                text.append(line).append("\n");
            }
            Element element = new Element(number, time, text.toString());
            elements.add(element);
        }
        return elements.size();
    }

    public void shift(int ms) {
        elements = elements.stream()
                .map(e -> e.shifted(ms))
                .collect(Collectors.toList());
    }

    public void find(String text) {
        elements.stream()
                .filter(e -> e.containsText(text))
                .map(Element::getNumber)
                .forEach(System.out::println);
    }
    public void print() {
        elements.forEach(System.out::println);
    }

}
