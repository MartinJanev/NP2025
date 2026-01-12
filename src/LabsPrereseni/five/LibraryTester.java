package LabsPrereseni.five;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


class Book {
    private String isbn;
    private String title;
    private String author;
    private int year;

    private int totalCopies;
    private int availableCopies;
    private int totalBorrows;

    private Set<Member> borrowers;
    private Queue<Member> waitingList;

    public Book(String isbn, String title, String author, int year) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.totalCopies = 1;
        this.availableCopies = 1;
        this.totalBorrows = 0;
        this.borrowers = new HashSet<>();
        this.waitingList = new LinkedList<>();
    }

    public void addCopy() {
        this.totalCopies++;
        this.availableCopies++;
    }

    public boolean hasCopies() {
        return availableCopies > 0;
    }

    public void incrAvailable() {
        availableCopies++;
    }

    public void decAvailable() {
        this.availableCopies--;
    }

    public void increaseBorrows() {
        this.totalBorrows++;
    }

    public void addBorrower(Member member) {
        borrowers.add(member);
    }

    public boolean memberInWaitingList(Member member) {
        return waitingList.contains(member);
    }

    public Set<Member> getBorrowers() {
        return borrowers;
    }

    public Queue<Member> getWaitingList() {
        return waitingList;
    }

    public void removeBorrower(Member member) {
        borrowers.remove(member);
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - \"%s\" by %s (%d), available: %d, total borrows: %d",
                isbn,
                title,
                author,
                year,
                availableCopies,
                totalBorrows
        );
    }

    public String getAuthor() {
        return author;
    }


}

class Member {

    private String ID;
    private String name;

    private Set<Book> currentlyBorrowedBooks;
    private int totalBorrowedBooks;

    public Member(String ID, String name) {
        this.ID = ID;
        this.name = name;
        this.currentlyBorrowedBooks = new HashSet<>();
        this.totalBorrowedBooks = 0;
    }

    public boolean hasBorrowed(Book b) {
        return currentlyBorrowedBooks.contains(b);
    }

    public void borrow(Book book) {
        if (currentlyBorrowedBooks.add(book)) {
            totalBorrowedBooks++;
        }
    }

    public void returnBook(Book book) {
        currentlyBorrowedBooks.remove(book);
    }

    public int getCurrentlyBorrowedBooksSize() {
        return currentlyBorrowedBooks.size();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format(
                "%s (%s) - borrowed now: %d, total borrows: %d",
                name,
                ID,
                currentlyBorrowedBooks.size(),
                totalBorrowedBooks
        );
    }

    public String getID() {
        return ID;
    }
}

class LibrarySystem {
    private String name;
    private Map<String, Book> booksbyISBN;
    private Map<String, Member> membersByID;

    public LibrarySystem(String name) {
        this.name = name;
        this.booksbyISBN = new HashMap<>();
        this.membersByID = new HashMap<>();
    }

    public void registerMember(String id, String fullName) {
        membersByID.putIfAbsent(id, new Member(id, fullName));
    }

    public void addBook(String isbn, String title, String author, int year) {
        booksbyISBN.compute(isbn, (k, exists) -> {
            if (exists == null) {
                return new Book(isbn, title, author, year);
            } else {
                exists.addCopy();
                return exists;
            }
        });
    }

    public void borrowBook(String memberId, String isbn) {
        Member member = membersByID.get(memberId);
        Book book = booksbyISBN.get(isbn);

        if (member == null || book == null) return;

        if (member.hasBorrowed(book)) return;

        if (book.hasCopies()) {
            book.decAvailable();
            book.increaseBorrows();
            member.borrow(book);
            book.addBorrower(member);
        } else {
            if (!book.memberInWaitingList(member)) {
                book.getWaitingList().add(member);
            }
        }
    }

    public void returnBook(String memberID, String isbn) {
        Member member = membersByID.get(memberID);
        Book book = booksbyISBN.get(isbn);

        if (member == null || book == null) return;
        if (!member.hasBorrowed(book)) return;

        member.returnBook(book);
        book.removeBorrower(member);
        book.incrAvailable();

        Queue<Member> queueForBook = book.getWaitingList();
        while (!queueForBook.isEmpty() && book.hasCopies()) {
            Member next = queueForBook.poll();
            if (next == null) break;
            if (next.hasBorrowed(book)) continue;

            book.decAvailable();
            book.increaseBorrows();
            next.borrow(book);
            book.addBorrower(next);
            break;
        }
    }

    public void printMembers() {
        membersByID.values().stream()
                .sorted(
                        Comparator.comparingInt(Member::getCurrentlyBorrowedBooksSize).reversed()
                                .thenComparing(Member::getName)
                )
                .forEach(System.out::println);
    }

    public void printBooks() {
        booksbyISBN.values().stream()
                .sorted(
                        Comparator.comparingInt(Book::getTotalBorrows).reversed()
                                .thenComparingInt(Book::getYear)
                )
                .forEach(System.out::println);
    }

    public void printBookCurrentBorrowers(String isbn) {
        Book book = booksbyISBN.get(isbn);
        if (book == null) {
            System.out.println();
            return;
        }

        System.out.println(book.getBorrowers().stream()
                .map(Member::getID)
                .sorted()
                .collect(Collectors.joining(", ")));
    }

    public void printTopAuthors() {
        Map<String, Integer> borrowCountByAuthor =
                booksbyISBN.values().stream()
                        .collect(
                                Collectors.groupingBy(
                                        Book::getAuthor,
                                        Collectors.summingInt(Book::getTotalBorrows)
                                ));

        borrowCountByAuthor.entrySet().stream()
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
}

public class LibraryTester {
    public static void main(String[] args) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            String libraryName = br.readLine();
            //   System.out.println(libraryName); //test
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

