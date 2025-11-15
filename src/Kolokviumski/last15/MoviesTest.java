package Kolokviumski.last15;

import java.util.*;
import java.util.stream.Collectors;

class Movie {
    String title;
    int[] ratings;
    double ratingCoef;

    public Movie(String title, int[] ratings) {
        this.title = title;
        this.ratings = ratings;
        this.ratingCoef = 0.0;
    }

    public String getTitle() {
        return title;
    }

    public double getRatingCoef() {
        return ratingCoef;
    }

    public void setRatingCoef(double ratingCoef) {
        this.ratingCoef = ratingCoef;
    }

    public double averageRating() {
        return Arrays.stream(ratings).sum() / (ratings.length * 1.0);
    }

    @Override
    public String toString() {
        return String.format("%s (%.2f) of %d ratings", title, averageRating(), ratings.length);
    }
}

class MoviesList {
    List<Movie> movies;

    public MoviesList() {
        movies = new ArrayList<>();
    }

    public void addMovie(String title, int[] ratings) {
        movies.add(new Movie(title, ratings));
    }

    public List<Movie> top10ByAvgRating() {
        return movies.stream().sorted(Comparator.comparingDouble(Movie::averageRating).reversed().thenComparing(Movie::getTitle)).limit(10).collect(Collectors.toList());
    }

    public List<Movie> top10ByRatingCoef() {
        int maxVal = movies.stream().mapToInt(movie -> movie.ratings.length).max().orElse(1);
        for (Movie movie : movies) {
            movie.setRatingCoef(movie.averageRating() * movie.ratings.length / (maxVal * 1.0));
        }
        return movies.stream().sorted(Comparator.comparingDouble(Movie::getRatingCoef).reversed().thenComparing(Movie::getTitle)).limit(10).collect(Collectors.toList());
    }
}

public class MoviesTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        MoviesList moviesList = new MoviesList();
        int n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int x = scanner.nextInt();
            int[] ratings = new int[x];
            for (int j = 0; j < x; ++j) {
                ratings[j] = scanner.nextInt();
            }
            scanner.nextLine();
            moviesList.addMovie(title, ratings);
        }
        scanner.close();
        List<Movie> movies = moviesList.top10ByAvgRating();
        System.out.println("=== TOP 10 BY AVERAGE RATING ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
        movies = moviesList.top10ByRatingCoef();
        System.out.println("=== TOP 10 BY RATING COEFFICIENT ===");
        for (Movie movie : movies) {
            System.out.println(movie);
        }
    }
}