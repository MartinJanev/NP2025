package Lab.Five;

import java.io.InputStream;
import java.util.*;

public class Anagrams {

    public static void main(String[] args) {
        findAll(System.in);
    }

    public static void findAll(InputStream inputStream) {
        /*
        Да се напише програма која од дадена листа со зборови (секој збор е во нов ред)
        ќе ги најде групите со пет или повеќе анаграми (анаграм е збор составен од истите букви).
         Откако ќе ги најде групите треба да се отпечататат на стандарден излез
         сортирани според азбучен ред и тоа секоја група од анаграми во нов ред,
         а анаграмите одделени со празно место (внимавајте да нема празно место на крајот од редот).
         Редоследот на печатење на групите од анаграми е соодветен на редоследот на зборовите кои
         дошле на влез како први преставници на соодветната група од анаграми.
         */

        Scanner scanner = new Scanner(inputStream);
        Map<String, TreeSet<String>> anagramGroups = new LinkedHashMap<>();

        while (scanner.hasNextLine()){
            String word = scanner.nextLine().trim();
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String sortedWord = new String(chars);

            anagramGroups.putIfAbsent(sortedWord, new TreeSet<>());
            anagramGroups.get(sortedWord).add(word);
        }


        for (Map.Entry<String, TreeSet<String>> entry : anagramGroups.entrySet()) {
            TreeSet<String> group = entry.getValue();
            if (group.size() >= 5) {
                Iterator<String> iterator = group.iterator();
                while (iterator.hasNext()) {
                    System.out.print(iterator.next());
                    if (iterator.hasNext()) {
                        System.out.print(" ");
                    }
                }
                System.out.println();
            }
        }
    }
}
