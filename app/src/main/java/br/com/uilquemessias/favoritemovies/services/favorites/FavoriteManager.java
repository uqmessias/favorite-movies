package br.com.uilquemessias.favoritemovies.services.favorites;

import android.content.Context;

import java.util.Collections;
import java.util.List;

import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Review;

public class FavoriteManager {

    private FavoriteMovieDbHelper mDbHelper;

    public FavoriteManager(final Context context) {
        mDbHelper = new FavoriteMovieDbHelper(context);
    }

    public Movie putMovie(final Movie movie) {
        try {
            mDbHelper.insertMovie(movie);
            return movie;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean containsMovieWithId(final int movieId) {
        if (movieId > 0) {
            return mDbHelper.containsMovieWithId(movieId);
        }

        return false;
    }

    public Movie getMovie(final int movieId) {
        if (movieId > 0) {
            try {
                return mDbHelper.getMovie(movieId);
            } catch (Exception e) {
                // nothing
                e.printStackTrace();
            }
        }

        return null;
    }

    public void removeMovie(final int movieId) {
        try {
            mDbHelper.removeMovie(movieId);
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }
    }

    public List<Review> putReviews(final int movieId, final List<Review> reviews) {
        if (movieId > 0) {
            try {
                mDbHelper.insertReviews(movieId, reviews);
                return reviews;
            } catch (Exception e) {
                // nothing
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    public List<Review> getReviews(final int movieId) {
        try {
            return mDbHelper.getReviews(movieId);
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public void removeReviews(final int movieId) {
        try {
            mDbHelper.removeReviews(movieId);
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }
    }

    public List<String> putVideos(final int movieId, final List<String> videos) {
        if (movieId > 0) {
            try {
                mDbHelper.insertVideos(movieId, videos);
                return videos;
            } catch (Exception e) {
                // nothing
                e.printStackTrace();
            }
        }

        return Collections.emptyList();
    }

    public List<String> getVideos(final int movieId) {
        try {
            return mDbHelper.getVideos(movieId);
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public void removeVideos(final int movieId) {
        try {
            mDbHelper.removeVideos(movieId);
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }
    }

    public List<Movie> listAllMovies() {
        try {
            return mDbHelper.getMovies();
        } catch (Exception e) {
            // nothing
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    public void close() {
        mDbHelper.close();
    }
}
