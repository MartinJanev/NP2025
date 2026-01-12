/*
Даден ви е стартен Java програм TextCounter.java кој чита повеќе текстови од стандарден влез и ги обработува со користење на конкурентно програмирање. Дел од кодот е намерно оставен недовршен. Ваша задача е да ги пополните овие делови со примена на концепти како Callable, ламбда-изрази, ExecutorService и Future.

Најпрво, треба да го имплементирате методот getTextCounter(int textId, String text). Овој метод мора да враќа Callable<Counter>. Не е дозволено да креирате посебна класа што имплементира Callable; задолжително е да користите ламбда-израз. Пресметката на статистиките за текстот мора да се извршува внатре во Callable, а не пред неговото креирање. Кога callable-задачата ќе се изврши, таа треба да го изброи бројот на линии во текстот, бројот на зборови (при што зборовите се разделени со еден или повеќе празнини) и бројот на карактери во текстот. Callable-задачата мора да врати нов објект од тип Counter, инициализиран со дадениот textId и пресметаните вредности.

Потоа, во методот main, по читањето на секој текст од стандарден влез, треба да креирате соодветен Callable<Counter> со повик на методот getTextCounter, користејќи ги соодветните аргументи. Секој креиран callable мора да се додаде во дадената листа tasks. За секој прочитан текст мора да постои точно една callable-задача.

Откако сите callable-задачи ќе бидат додадени во листата, треба да ги извршите конкурентно со користење на дадениот ExecutorService. Потребно е да ги повикате сите задачи преку извршувачот и да добиете листа од Future објекти кои ги претставуваат резултатите од конкурентните пресметки. Може да користите било кој соодветен метод од ExecutorService кој извршува callable-задачи и враќа futures.

По извршувањето на задачите, треба да ги извлечете резултатите од futures. За секој future, преземете го соодветниот Counter објект и додајте го во листата results. Овој чекор треба коректно да го земе предвид фактот дека редоследот на извршување на задачите не е загарантиран.

Не смеете да менувате други делови од кодот. Посебно, не смеете да го менувате форматот на влезот, структурата на класата Counter, ниту логиката за сортирање и печатење на крајот од програмата. Крајниот излез на програмата мора да ги прикаже статистиките за сите текстови, сортирани по textId, точно како што е дефинирано во дадениот код.
 */

package Lab.Seven.dop1;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextCounter {

    // Result holder
    public static class Counter {
        public final int textId;
        public final int lines;
        public final int words;
        public final int chars;

        public Counter(int textId, int lines, int words, int chars) {
            this.textId = textId;
            this.lines = lines;
            this.words = words;
            this.chars = chars;
        }

        @Override
        public String toString() {
            return "Counter{" +
                    "textId=" + textId +
                    ", lines=" + lines +
                    ", words=" + words +
                    ", chars=" + chars +
                    '}';
        }


    }

    //TODO
    public static Callable<Counter> getTextCounter(int textId, String text) {
        return () -> {
            int lines = text.split("\n", -1).length;
            int words = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
            int chars = text.length();
            return new Counter(textId, lines, words, chars);
        };
    }


    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        int n = sc.nextInt();       // number of texts
        sc.nextLine();              // consume newline

        List<Callable<Counter>> tasks = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            int textId = sc.nextInt();
            sc.nextLine();          // consume newline

            int lines = sc.nextInt();   // number of lines for this text
            sc.nextLine();              // consume newline

            StringBuilder text = new StringBuilder();
            for (int j = 0; j < lines; j++) {
                text.append(sc.nextLine());
                if (j < lines - 1) {
                    text.append("\n");
                }
            }


            //TODO add a Callable<Counter> for each text read in the tasks list
            tasks.add(getTextCounter(textId, text.toString()));
        }

        ExecutorService executor =
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        //TODO invoke All tasks on the executor and create a List<Future<?>>
        List<Future<Counter>> futures = executor.invokeAll(tasks);


        List<Counter> results = new ArrayList<>();


        //TODO extract results from the List<Future>
        for (Future<Counter> future : futures) {
            results.add(future.get());
        }

        //TODO Additional task

        Callable<Counter> aggregateTask = () -> {
            int totalLines = 0;
            int totalWords = 0;
            int totalChars = 0;
            for (Counter c : results) {
                totalLines += c.lines;
                totalWords += c.words;
                totalChars += c.chars;
            }
            return new Counter(-1, totalLines, totalWords, totalChars);
        };

        Future<Counter> aggregateFuture = executor.submit(aggregateTask);
        Counter aggregateResult = aggregateFuture.get();

        executor.shutdown();


        // Sorting by textId (important concept!)
        results.sort(Comparator.comparingInt(c -> c.textId));

        // Output (optional for debugging / demonstration)
        for (Counter c : results) {
            System.out.printf(
                    "%d %d %d %d%n",
                    c.textId, c.lines, c.words, c.chars
            );
        }

        System.out.printf(
                "%d %d %d %d%n",
                aggregateResult.textId,
                aggregateResult.lines,
                aggregateResult.words,
                aggregateResult.chars
        );
    }
}

