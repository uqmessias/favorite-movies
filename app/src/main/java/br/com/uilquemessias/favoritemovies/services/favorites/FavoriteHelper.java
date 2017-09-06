package br.com.uilquemessias.favoritemovies.services.favorites;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Review;

public class FavoriteHelper {

    private FavoriteHelper() {
        // do nothing
    }

    private static String getStringFromCursor(final Cursor cursor, final String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }

    public static Movie getMovieFromCursor(final Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            final Movie movie = new Movie();
            movie.setId(cursor.getInt(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry._ID)));
            movie.setTitle(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE));
            movie.setOverview(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW));
            movie.setBackdropPath(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH));
            movie.setPosterPath(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH));
            movie.setReleaseDate(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
            movie.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));

            return movie;
        }

        return null;
    }

    public static ContentValues getContentValuesFromMovie(final Movie movie) {
        final ContentValues movieValues = new ContentValues();
        movieValues.put(FavoriteMovieContract.MovieEntry._ID, movie.getId());
        movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
        movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
        movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH, movie.getBackdropPath());
        movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH, movie.getPosterPath());
        movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
        return movieValues;
    }

    public static ContentValues[] getContentsValuesFromReviews(final int movieId, final List<Review> reviews) {
        final ContentValues[] rvs = new ContentValues[reviews.size()];

        for (int rI = 0; rI < reviews.size(); rI++) {
            final ContentValues reviewValue = new ContentValues();
            final Review review = reviews.get(rI);
            reviewValue.put(FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID, movieId);
            reviewValue.put(FavoriteMovieContract.ReviewEntry.COLUMN_NAME_AUTHOR, review.getAuthor());
            reviewValue.put(FavoriteMovieContract.ReviewEntry.COLUMN_NAME_CONTENT, review.getContent());
            rvs[rI] = reviewValue;

        }

        return rvs;
    }

    public static ContentValues[] getContentsValuesFromVideos(final int movieId, final List<String> videos) {
        final ContentValues[] vvs = new ContentValues[videos.size()];

        for (int vI = 0; vI < videos.size(); vI++) {
            final ContentValues videoValue = new ContentValues();
            final String video = videos.get(vI);
            videoValue.put(FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID, movieId);
            videoValue.put(FavoriteMovieContract.VideoEntry.COLUMN_NAME_KEY, video);
            vvs[vI] = videoValue;
        }

        return vvs;
    }
}
