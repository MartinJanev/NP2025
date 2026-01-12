package Lab.Four;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class TermFrequency {

    Set<String> stopWords;
    Map<String, Integer> termFreq;
    int totalWords;
    Set<String> distinctWords;

    public TermFrequency(InputStream inputStream, String[] stopWords) {
        this.stopWords = Arrays.stream(stopWords).collect(Collectors.toSet());
        termFreq = new HashMap<>();
        distinctWords = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\s+");
                for (String word : words) {
                    word = word.replaceAll("[.,]", "");
                    if (!this.stopWords.contains(word) && !word.isEmpty()) {
                        termFreq.put(word, termFreq.getOrDefault(word, 0) + 1);
                        ++totalWords;
                        distinctWords.add(word);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public int countTotal() {
        return totalWords;
    }

    public int countDistinct() {
        return distinctWords.size();
    }

    public List<String> mostOften(int k) {
        return termFreq.entrySet()
                .stream()
                .sorted((e1, e2) -> {
                    int freqCompare = e2.getValue().compareTo(e1.getValue());
                    if (freqCompare == 0) {
                        return e1.getKey().compareTo(e2.getKey());
                    }
                    return freqCompare;
                })
                .limit(k)
                .map(Entry::getKey)
                .collect(Collectors.toList());
    }

}

public class TermFrequencyTest {
    public static void main(String[] args) throws FileNotFoundException {
        String[] stop = new String[]{"во", "и", "се", "за", "ќе", "да", "од",
                "ги", "е", "со", "не", "тоа", "кои", "до", "го", "или", "дека",
                "што", "на", "а", "но", "кој", "ја"};
        TermFrequency tf = new TermFrequency(System.in,
                stop);
        System.out.println(tf.countTotal());
        System.out.println(tf.countDistinct());
        System.out.println(tf.mostOften(10));
    }
}
// vasiot kod ovde
