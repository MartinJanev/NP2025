package Lab.Three.dop2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


class Movie {
    private String title, genre;
    int year;
    double averageRating;

    public Movie(String title, String genre, int year, double averageRating) {
        this.title = title;
        this.genre = genre;
        this.year = year;
        this.averageRating = averageRating;
    }


    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public double getAverageRating() {
        return averageRating;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %d, %.2f", title, genre, year, averageRating);
    }
}

class MovieTheater {

    ArrayList<Movie> movies;

    public MovieTheater() {
        movies = new ArrayList<Movie>();
    }

    public void readMovies(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        int movieCount = Integer.parseInt(br.readLine().trim()); // Read the number of movies
        String title;
        while (movieCount > 0 && (title = br.readLine()) != null && !title.isEmpty()) {
            String genre = br.readLine();
            if (genre == null) break;
            String yearLine = br.readLine();
            if (yearLine == null) break;
            int year = Integer.parseInt(yearLine.trim());
            String ratingLine = br.readLine();
            if (ratingLine == null) break;

            double averageRating = Arrays
                    .stream(ratingLine.trim().split("\\s+"))
                    .mapToDouble(Double::parseDouble)
                    .sum() / ratingLine.trim().split("\\s+").length;

            movies.add(new Movie(title, genre, year, averageRating));
            movieCount--; // Decrement the count after processing a movie
        }
    }

    public void printByRatingAndTitle() {
        movies.stream()
                .sorted(Comparator.comparing(Movie::getAverageRating).reversed()
                        .thenComparing(Movie::getTitle))
                .forEach(System.out::println);
    }

    public void printByGenreAndTitle() {
        movies.stream()
                .sorted(Comparator.comparing(Movie::getGenre)
                        .thenComparing(Movie::getTitle))
                .forEach(System.out::println);
    }

    public void printByYearAndTitle() {
        movies.stream()
                .sorted(Comparator.comparing(Movie::getYear)
                        .thenComparing(Movie::getTitle))
                .forEach(System.out::println);
    }

    public Map<String, Double> ratingByGenre() {
        Map<String, Double> ratings = new HashMap<>();
        List<String> genres = movies.stream()
                .map(Movie::getGenre)
                .collect(Collectors.toList());
        for (String genre : genres) {
            ratings.put(genre, movies.stream().filter(m -> m.getGenre().equals(genre)).mapToDouble(Movie::getAverageRating).sum());
        }
        return ratings;
    }

    public Map<String, List<Movie>> groupByGenre() {
        Map<String, List<Movie>> groupedMovies = new HashMap<>();
        List<String> genres = movies.stream()
                .map(Movie::getGenre)
                .collect(Collectors.toList());

        for (String genre : genres) {
            groupedMovies.put(genre, movies.stream().filter(m -> m.getGenre().equals(genre)).toList());
        }

        return groupedMovies;
    }

    public void printGroupMap(Map<String, List<Movie>> groupedMovies) {
        groupedMovies.keySet()
                .forEach(genre -> {
                    System.out.println(genre + "\t");
                    List<Movie> movies = groupedMovies.get(genre);
                    movies.forEach(System.out::println);
                    System.out.println();
                });
    }

    public void printRate(Map<String, Double> ratings) {
        ratings.keySet().forEach
                (genre -> System.out.println(String.format("%s - %.2f", genre, ratings.get(genre))));
    }
}


public class MovieTheaterTester2 {
    public static void main(String[] args) {
        MovieTheater mt = new MovieTheater();
        try {
            mt.readMovies(System.in);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("SORTING BY RATING");
        mt.printByRatingAndTitle();
        System.out.println("\nSORTING BY GENRE");
        mt.printByGenreAndTitle();
        System.out.println("\nSORTING BY YEAR");
        mt.printByYearAndTitle();
        System.out.println("\nGROUPED BY GENRE");
        Map<String, List<Movie>> map1 = mt.groupByGenre();
        mt.printGroupMap(map1);
        System.out.println("\nRATING BY GENRE");
        Map<String, Double> map2 = mt.ratingByGenre();
        mt.printRate(map2);

    }
}
