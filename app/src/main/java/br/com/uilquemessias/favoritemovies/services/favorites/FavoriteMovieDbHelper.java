package br.com.uilquemessias.favoritemovies.services.favorites;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Review;

public class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    private final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
            FavoriteMovieContract.MovieEntry.TABLE_NAME + " (" +
            FavoriteMovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL NOT NULL);";
    private final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " +
            FavoriteMovieContract.ReviewEntry.TABLE_NAME + " (" +
            FavoriteMovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            FavoriteMovieContract.ReviewEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL," +
            FavoriteMovieContract.ReviewEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL);";
    private final String SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " +
            FavoriteMovieContract.VideoEntry.TABLE_NAME + " (" +
            FavoriteMovieContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            FavoriteMovieContract.VideoEntry.COLUMN_NAME_KEY + " TEXT NOT NULL);";

    private final String SQL_DROP_MOVIE_TABLE = "DROP TABLE IF EXISTS " + FavoriteMovieContract.MovieEntry.TABLE_NAME;
    private final String SQL_DROP_REVIEW_TABLE = "DROP TABLE IF EXISTS " + FavoriteMovieContract.ReviewEntry.TABLE_NAME;
    private final String SQL_DROP_VIDEO_TABLE = "DROP TABLE IF EXISTS " + FavoriteMovieContract.VideoEntry.TABLE_NAME;

    public FavoriteMovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
        db.execSQL(SQL_CREATE_VIDEO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_MOVIE_TABLE);
        db.execSQL(SQL_DROP_REVIEW_TABLE);
        db.execSQL(SQL_DROP_VIDEO_TABLE);
        onCreate(db);
    }

    public boolean containsMovieWithId(final int movieId) {
        Cursor cursor = null;

        try {
            final SQLiteDatabase db = getReadableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            cursor = db.rawQuery("SELECT _ID FROM " +
                    FavoriteMovieContract.MovieEntry.TABLE_NAME +
                    " WHERE _ID = ?", selectionArgs
            );

            if (cursor.getCount() > 0) {
                return true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;
    }

    public long insertMovie(final Movie movie) throws Exception {
        try {
            SQLiteDatabase db = getWritableDatabase();
            final ContentValues movieValues = new ContentValues();
            movieValues.put(FavoriteMovieContract.MovieEntry._ID, movie.getId());
            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE, movie.getTitle());
            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW, movie.getOverview());
            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH, movie.getBackdropPath());
            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH, movie.getPosterPath());
            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE, movie.getReleaseDate());
            movieValues.put(FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE, movie.getVoteAverage());
            return db.insert(FavoriteMovieContract.MovieEntry.TABLE_NAME, null, movieValues);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public Movie getMovie(final int movieId) throws Resources.NotFoundException {
        Cursor cursor = null;

        try {
            final SQLiteDatabase db = getReadableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            cursor = db.query(FavoriteMovieContract.MovieEntry.TABLE_NAME, null,
                    FavoriteMovieContract.MovieEntry._ID + "=?", selectionArgs,
                    null, null, null);

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                final Movie movie = new Movie();
                movie.setId(movieId);
                movie.setTitle(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE));
                movie.setOverview(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW));
                movie.setBackdropPath(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH));
                movie.setPosterPath(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH));
                movie.setReleaseDate(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
                movie.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));

                return movie;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        throw new Resources.NotFoundException("Movie with id " + movieId + " does not exists");
    }

    public List<Movie> getMovies() throws Resources.NotFoundException {
        Cursor cursor = null;
        final List<Movie> movies = new ArrayList<>();

        try {
            final SQLiteDatabase db = getReadableDatabase();
            cursor = db.query(FavoriteMovieContract.MovieEntry.TABLE_NAME, null, null, null, null, null, null);

            while (cursor.moveToNext()) {
                final Movie movie = new Movie();
                movie.setId(cursor.getInt(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry._ID)));
                movie.setTitle(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE));
                movie.setOverview(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW));
                movie.setBackdropPath(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH));
                movie.setPosterPath(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH));
                movie.setReleaseDate(getStringFromCursor(cursor, FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE));
                movie.setVoteAverage(cursor.getFloat(cursor.getColumnIndex(FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE)));

                movies.add(movie);
            }

            if (!movies.isEmpty()) {
                return movies;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        throw new Resources.NotFoundException("There's no movie");
    }

    public void removeMovie(final int movieId) {
        try {
            final SQLiteDatabase db = getWritableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            db.delete(FavoriteMovieContract.MovieEntry.TABLE_NAME, FavoriteMovieContract.MovieEntry._ID + "=?", selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void insertReviews(final int movieId, final List<Review> reviews) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (final Review review : reviews) {
                final ContentValues reviewValues = new ContentValues();
                reviewValues.put(FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID, movieId);
                reviewValues.put(FavoriteMovieContract.ReviewEntry.COLUMN_NAME_AUTHOR, review.getAuthor());
                reviewValues.put(FavoriteMovieContract.ReviewEntry.COLUMN_NAME_CONTENT, review.getContent());

                db.insert(FavoriteMovieContract.ReviewEntry.TABLE_NAME, null, reviewValues);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public List<Review> getReviews(final int movieId) throws Resources.NotFoundException {
        final List<Review> reviews = new ArrayList<>();
        Cursor cursor = null;

        try {
            final SQLiteDatabase db = getReadableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            cursor = db.query(FavoriteMovieContract.ReviewEntry.TABLE_NAME, null,
                    FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + "=?",
                    selectionArgs, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    final Review review = new Review();
                    review.setId(getStringFromCursor(cursor, FavoriteMovieContract.ReviewEntry._ID));
                    review.setAuthor(getStringFromCursor(cursor, FavoriteMovieContract.ReviewEntry.COLUMN_NAME_AUTHOR));
                    review.setContent(getStringFromCursor(cursor, FavoriteMovieContract.ReviewEntry.COLUMN_NAME_CONTENT));
                    reviews.add(review);
                }
            }

            if (!reviews.isEmpty()) {
                return reviews;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        throw new Resources.NotFoundException("Review with Movie id " + movieId + " does not exists");
    }

    public void removeReviews(final int movieId) {
        try {
            final SQLiteDatabase db = getWritableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            db.delete(FavoriteMovieContract.ReviewEntry.TABLE_NAME, FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + "=?", selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void insertVideos(final int movieId, final List<String> videos) throws Exception {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            for (final String video : videos) {
                final ContentValues videoValues = new ContentValues();
                videoValues.put(FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID, movieId);
                videoValues.put(FavoriteMovieContract.VideoEntry.COLUMN_NAME_KEY, video);

                db.insert(FavoriteMovieContract.VideoEntry.TABLE_NAME, null, videoValues);
            }

            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            db.endTransaction();
        }
    }

    public List<String> getVideos(final int movieId) throws Resources.NotFoundException {
        final List<String> videos = new ArrayList<>();
        Cursor cursor = null;

        try {
            final SQLiteDatabase db = getReadableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            cursor = db.query(FavoriteMovieContract.VideoEntry.TABLE_NAME, null,
                    FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID + "=?",
                    selectionArgs, null, null, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    final String video = getStringFromCursor(cursor, FavoriteMovieContract.VideoEntry.COLUMN_NAME_KEY);
                    videos.add(video);
                }
            }

            if (videos.isEmpty()) {
                throw new Resources.NotFoundException("Video with Movie id " + movieId + " does not exists");
            }

            return videos;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void removeVideos(final int movieId) {
        try {
            final SQLiteDatabase db = getWritableDatabase();
            final String[] selectionArgs = {String.valueOf(movieId)};
            db.delete(FavoriteMovieContract.VideoEntry.TABLE_NAME, FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID + "=?", selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String getStringFromCursor(final Cursor cursor, final String columnName) {
        return cursor.getString(cursor.getColumnIndex(columnName));
    }
}
