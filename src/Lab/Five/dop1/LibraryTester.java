package Lab.Five.dop1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

class Book {
    private final String ISBN;
    private final String title;
    private final String author;
    private final int year;

    private int totalCopies;
    private int availableCopies;
    private int totalBorrows;

    private final Set<Member> currentBorrowers;
    private final Queue<Member> waitingList;

    public Book(String ISBN, String title, String author, int year) {
        this.ISBN = ISBN;
        this.title = title;
        this.author = author;
        this.year = year;
        this.totalCopies = 1;
        this.availableCopies = 1;
        this.totalBorrows = 0;
        this.currentBorrowers = new HashSet<>();
        this.waitingList = new LinkedList<>();
    }

    public String getISBN() {
        return ISBN;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public void addCopy() {
        totalCopies++;
        availableCopies++;
    }

    public void incrementTotalBorrows() {
        totalBorrows++;
    }

    public void decrementAvailable() {
        availableCopies--;
    }

    public void incrementAvailable() {
        availableCopies++;
    }

    public Set<Member> getCurrentBorrowers() {
        return currentBorrowers;
    }

    public Queue<Member> getWaitingList() {
        return waitingList;
    }

    public void addBorrower(Member m) {
        currentBorrowers.add(m);
    }

    public void removeBorrower(Member m) {
        currentBorrowers.remove(m);
    }

    public boolean hasAvailableCopy() {
        return availableCopies > 0;
    }

    public boolean isInWaitingList(Member m) {
        return waitingList.contains(m);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book book = (Book) o;
        return Objects.equals(ISBN, book.ISBN);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ISBN);
    }
}

class Member {
    private final String memberID;
    private final String fullName;

    private final Set<Book> currentlyBorrowed;
    private int totalBorrows;

    public Member(String memberID, String fullName) {
        this.memberID = memberID;
        this.fullName = fullName;
        this.currentlyBorrowed = new HashSet<>();
        this.totalBorrows = 0;
    }

    public String getMemberID() {
        return memberID;
    }

    public String getFullName() {
        return fullName;
    }

    public int getCurrentlyBorrowedCount() {
        return currentlyBorrowed.size();
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public boolean hasBorrowed(Book b) {
        return currentlyBorrowed.contains(b);
    }

    public void borrowBook(Book b) {
        // ако не ја има веќе, ја додава и зголемува totalBorrows
        if (currentlyBorrowed.add(b)) {
            totalBorrows++;
        }
    }

    public void returnBook(Book b) {
        currentlyBorrowed.remove(b);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member)) return false;
        Member member = (Member) o;
        return Objects.equals(memberID, member.memberID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberID);
    }
}

class LibrarySystem {
    private final String libraryName;
    private final Map<String, Member> membersById;
    private final Map<String, Book> booksByIsbn;

    public LibrarySystem(String libraryName) {
        this.libraryName = libraryName;
        this.membersById = new HashMap<>();
        this.booksByIsbn = new HashMap<>();
    }

    public void registerMember(String id, String fullName) {
        // регистрира член ако не постои
        membersById.putIfAbsent(id, new Member(id, fullName));
    }

    public void addBook(String isbn, String title, String author, int year) {
//        booksByIsbn.compute(isbn, (k, existing) -> {
//            if (existing == null) {
//                return new Book(isbn, title, author, year);
//            } else {
//                existing.addCopy();
//                return existing;
//            }
//        });

        booksByIsbn.computeIfAbsent(isbn, k -> new Book(isbn, title, author, year))
                .addCopy();
    }

    public void borrowBook(String memberId, String isbn) {
        Member member = membersById.get(memberId);
        Book book = booksByIsbn.get(isbn);

        if (member == null || book == null) return;

        // ако веќе ја има книгата - игнорирај
        if (member.hasBorrowed(book)) return;

        if (book.hasAvailableCopy()) {
            // директно позајмување
            book.decrementAvailable();
            book.incrementTotalBorrows();
            member.borrowBook(book);
            book.addBorrower(member);
        } else {
            // нема слободни примероци -> во листа на чекање
            if (!book.isInWaitingList(member)) {
                book.getWaitingList().add(member);
            }
        }
    }

    public void returnBook(String memberId, String isbn) {
        Member member = membersById.get(memberId);
        Book book = booksByIsbn.get(isbn);

        if (member == null || book == null) return;
        if (!member.hasBorrowed(book)) return;

        // членот ја враќа книгата
        member.returnBook(book);
        book.removeBorrower(member);
        book.incrementAvailable();

        // ако има чекачи, првиот добива книга автоматски
        Queue<Member> waitList = book.getWaitingList();
        while (!waitList.isEmpty() && book.getAvailableCopies() > 0) {
            Member next = waitList.poll();
            if (next == null) break;
            // ако некако во меѓувреме ја добил книгата, скокни
            if (next.hasBorrowed(book)) continue;

            book.decrementAvailable();
            book.incrementTotalBorrows();
            next.borrowBook(book);
            book.addBorrower(next);
            // само еден член добива во овој момент (по услов)
            break;
        }
    }

    public void printMembers() {
        membersById.values().stream()
                .sorted(
                        Comparator.comparingInt(Member::getCurrentlyBorrowedCount).reversed()
                                .thenComparing(Member::getFullName)
                )
                .forEach(m -> System.out.printf(
                        "%s (%s) - borrowed now: %d, total borrows: %d%n",
                        m.getFullName(),
                        m.getMemberID(),
                        m.getCurrentlyBorrowedCount(),
                        m.getTotalBorrows()
                ));
    }

    public void printBooks() {
        booksByIsbn.values().stream()
                .sorted(
                        Comparator.comparingInt(Book::getTotalBorrows).reversed()
                                .thenComparingInt(Book::getYear)
                )
                .forEach(b -> System.out.printf(
                        "%s - \"%s\" by %s (%d), available: %d, total borrows: %d%n",
                        b.getISBN(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getYear(),
                        b.getAvailableCopies(),
                        b.getTotalBorrows()
                ));
    }

    public void printBookCurrentBorrowers(String isbn) {
        Book book = booksByIsbn.get(isbn);
        if (book == null) {
            System.out.println();
            return;
        }

        String result = book.getCurrentBorrowers().stream()
                .map(Member::getMemberID)
                .sorted()
                .collect(Collectors.joining(", "));

        System.out.println(result);
    }

    public void printTopAuthors() {
        Map<String, Integer> borrowsByAuthor = booksByIsbn.values().stream()
                .collect(Collectors.groupingBy(
                        Book::getAuthor,
                        Collectors.summingInt(Book::getTotalBorrows)
                ));

        borrowsByAuthor.entrySet().stream()
                .sorted(
                        Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
                                .reversed()
                                .thenComparing(Map.Entry::getKey)
                )
                .forEach(e -> System.out.printf(
                        "%s - %d%n",
                        e.getKey(),
                        e.getValue()
                ));
    }

    public Map<Book, Integer> getBooksAndNumberOfBorrowings() {
        return booksByIsbn.values()
                .stream()
                .collect(Collectors.toMap(
                        b -> b,
                        Book::getTotalBorrows
                ))
                .entrySet()
                .stream()
                .sorted(Comparator.comparing(e -> e.getKey().getTitle()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, TreeSet<String>> getAuthorsWithBooks() {
        return booksByIsbn.values()
                .stream()
                .collect(Collectors.groupingBy(
                        Book::getAuthor,
                        Collectors.mapping(
                                Book::getTitle,
                                Collectors.toCollection(TreeSet::new)
                        )
                ));
    }
}

public class LibraryTester {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            String libraryName = br.readLine();
            if (libraryName == null) return;

            libraryName = libraryName.trim();
            LibrarySystem lib = new LibrarySystem(libraryName);

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.equals("END")) break;
                if (line.isEmpty()) continue;

                String[] parts = line.split(" ");

                switch (parts[0]) {

                    case "registerMember": {
                        lib.registerMember(parts[1], parts[2]);
                        break;
                    }

                    case "addBook": {
                        String isbn = parts[1];
                        String title = parts[2];
                        String author = parts[3];
                        int year = Integer.parseInt(parts[4]);
                        lib.addBook(isbn, title, author, year);
                        break;
                    }

                    case "borrowBook": {
                        lib.borrowBook(parts[1], parts[2]);
                        break;
                    }

                    case "returnBook": {
                        lib.returnBook(parts[1], parts[2]);
                        break;
                    }

                    case "printMembers": {
                        lib.printMembers();
                        break;
                    }

                    case "printBooks": {
                        lib.printBooks();
                        break;
                    }

                    case "printBookCurrentBorrowers": {
                        lib.printBookCurrentBorrowers(parts[1]);
                        break;
                    }

                    case "printTopAuthors": {
                        lib.printTopAuthors();
                        break;
                    }

                    default:
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
