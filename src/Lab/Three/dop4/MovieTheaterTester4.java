package Lab.Three.dop4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;


class Movie {
    private String title, genre;
    private int year;
    private double averageRating;

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
    Map<String, Set<String>> actorsByMovie;


    public MovieTheater() {
        movies = new ArrayList<Movie>();
        actorsByMovie = new HashMap<>();
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

    public void addActors(String movieTitle, List<String> actors) {
        actorsByMovie.computeIfAbsent(movieTitle, k -> new HashSet<>()).addAll(actors);
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

    public Map<String, Movie> bestMovieByGenre() {
        Map<String, Movie> bestByGenre = new HashMap<>();
        List<String> genres = movies.stream()
                .map(Movie::getGenre)
                .collect(Collectors.toList());

        for (String genre : genres) {
            Movie best = movies.stream()
                    .filter(m -> m.getGenre().equals(genre))
                    .max(Comparator.comparing(Movie::getAverageRating))
                    .get();
            bestByGenre.put(genre, best);
        }
        return bestByGenre;
    }

    public void printBestMovieByGenre(Map<String, Movie> bestByGenre) {
        bestByGenre.keySet()
                .forEach(genre -> {
                    System.out.printf(String.format("%s \t %s \n", genre, bestByGenre.get(genre)));
                });
    }
}


public class MovieTheaterTester4 {
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
//        System.out.println("\nBEST MOVIE BY GENRE");
//        Map<String, Movie> bestByGenre = mt.bestMovieByGenre();
//        mt.printBestMovieByGenre(bestByGenre);


    }
}
