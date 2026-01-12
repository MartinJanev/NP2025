package LabsPrereseni.five;

import java.io.InputStream;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }


    public static void findAll(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        Map<String, TreeSet<String>> anagramGroups = new LinkedHashMap<>();

        while (scanner.hasNextLine()) {
            String word = scanner.nextLine().trim();
            char[] charovi = word.toCharArray();
            Arrays.sort(charovi);
            String sortiranZbor = new String(charovi);

            anagramGroups.putIfAbsent(sortiranZbor, new TreeSet<>());
            anagramGroups.get(sortiranZbor).add(word);
        }

        for (Map.Entry<String, TreeSet<String>> entry : anagramGroups.entrySet()) {
            TreeSet<String> grupa = entry.getValue();
            if (grupa.size()>=5){
                Iterator<String> stringIterator = grupa.iterator();
                while (stringIterator.hasNext()){
                    System.out.print(stringIterator.next());
                    if (stringIterator.hasNext()){
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }
}
