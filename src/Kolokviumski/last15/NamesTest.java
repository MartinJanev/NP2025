package Kolokviumski.last15;

import java.util.*;

public class NamesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        Names names = new Names();
        for (int i = 0; i < n; ++i) {
            String name = scanner.nextLine();
            names.addName(name);
        }
        n = scanner.nextInt();
        System.out.printf("===== PRINT NAMES APPEARING AT LEAST %d TIMES =====\n", n);
        names.printN(n);
        System.out.println("===== FIND NAME =====");
        int len = scanner.nextInt();
        int index = scanner.nextInt();
        System.out.println(names.findName(len, index));
        scanner.close();

    }
}

class Names {
    Map<String, Integer> nameCounter;

    public Names() {
        nameCounter = new TreeMap<>();
    }

    public void addName(String name) {
        if (!nameCounter.containsKey(name)) {
            nameCounter.put(name, 1);
        } else {
            nameCounter.put(name, nameCounter.get(name) + 1);
        }
    }

    private int uniques(String name) {
        char[] chars = name.toCharArray();
        Set<Character> charovi = new TreeSet<>();
        for (char aChar : chars) {
            charovi.add(aChar);
        }
        return charovi.size();
    }

    public void printN(int n) {
        nameCounter.keySet()
                .stream()
                .sorted(String::compareTo)
                .forEach(key -> {
                    int occ = nameCounter.get(key);
                    if (occ - n >= 0) {
                        System.out.printf("%s (%d) %d\n", key, occ, uniques(key.toLowerCase()));
                    }
                });
    }

    public String findName(int len, int x) {
        List<String> names = new LinkedList<>(nameCounter.keySet());
        ListIterator<String> it = names.listIterator();
        while (it.hasNext()) {
            if (it.next().length() >= len) it.remove();
        }

        it = names.listIterator();
        String name = null;
        x = x % names.size();
        for (int i = 0; i <= x; i++) {
            name = it.next();
        }
        return name;
    }
}
