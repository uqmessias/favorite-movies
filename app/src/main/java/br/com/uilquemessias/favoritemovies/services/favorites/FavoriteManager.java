package br.com.uilquemessias.favoritemovies.services.favorites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Review;

public class FavoriteManager {
    private static FavoriteManager sInstance;
    private final Map<Integer, Movie> mMovies = new HashMap<>();
    private final Map<Integer, List<Review>> mReviewsByMovie = new HashMap<>();
    private final Map<Integer, List<String>> mVideosByMovie = new HashMap<>();

    public static FavoriteManager instance() {
        if (sInstance == null) {
            sInstance = new FavoriteManager();
        }

        return sInstance;
    }

    private FavoriteManager() {
        // private constructor
    }

    public Movie putMovie(final Movie movie) {
        if (movie != null && movie.getId() > 0) {
            return mMovies.put(movie.getId(), movie);
        }

        return null;
    }

    public Movie getMovie(final int movieId) {
        return mMovies.get(movieId);
    }

    public Movie removeMovie(final int movieId) {
        return mMovies.remove(movieId);
    }

    public List<Review> putReviews(final int movieId, final List<Review> reviews) {
        if (movieId > 0) {
            return mReviewsByMovie.put(movieId, reviews);
        }

        return Collections.emptyList();
    }

    public List<Review> getReviews(final int movieId) {
        return mReviewsByMovie.get(movieId);
    }

    public List<Review> removeReviews(final int movieId) {
        return mReviewsByMovie.remove(movieId);
    }

    public List<String> putVideos(final int movieId, final List<String> videos) {
        if (movieId > 0) {
            return mVideosByMovie.put(movieId, videos);
        }

        return Collections.emptyList();
    }

    public List<String> getVideos(final int movieId) {
        return mVideosByMovie.get(movieId);
    }

    public List<String> removeVideos(final int movieId) {
        return mVideosByMovie.remove(movieId);
    }

    public List<Movie> listAllMovies() {
        final List<Movie> movies = new ArrayList<>();
        movies.addAll(mMovies.values());
        return movies;
    }
}
