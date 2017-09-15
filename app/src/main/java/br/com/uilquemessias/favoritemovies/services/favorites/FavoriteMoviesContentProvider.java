package br.com.uilquemessias.favoritemovies.services.favorites;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class FavoriteMoviesContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int REVIEWS = 200;
    public static final int VIDEOS = 300;
    public static final int MOVIE_WITH_ID = 101;
    public static final int REVIEWS_WITH_MOVIE_ID = 102;
    public static final int VIDEOS_WITH_MOVIE_ID = 103;
    public static final int REVIEW_WITH_ID = 201;
    public static final int VIDEO_WITH_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_REVIEWS, REVIEWS);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_VIDEOS, VIDEOS);

        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_MOVIES + "/" + FavoriteMovieContract.PATH_REVIEWS + "/#", REVIEWS_WITH_MOVIE_ID);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_MOVIES + "/" + FavoriteMovieContract.PATH_VIDEOS + "/#", VIDEOS_WITH_MOVIE_ID);

        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_REVIEWS + "/#", REVIEW_WITH_ID);
        uriMatcher.addURI(FavoriteMovieContract.AUTHORITY, FavoriteMovieContract.PATH_VIDEOS + "/#", VIDEO_WITH_ID);

        return uriMatcher;
    }

    private FavoriteMovieDbHelper mFavoriteMovieDbHelper;

    @Override
    public boolean onCreate() {
        mFavoriteMovieDbHelper = new FavoriteMovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                cursor = db.query(FavoriteMovieContract.MovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                cursor = db.query(FavoriteMovieContract.MovieEntry.TABLE_NAME, projection,
                        FavoriteMovieContract.MovieEntry._ID + "=?", new String[]{movieId},
                        null, null, sortOrder);
                break;
            case REVIEWS_WITH_MOVIE_ID:
                String movieIdForReview = uri.getPathSegments().get(2);
                cursor = db.query(FavoriteMovieContract.ReviewEntry.TABLE_NAME, projection,
                        FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{movieIdForReview}, null, null, sortOrder);
                break;
            case VIDEOS_WITH_MOVIE_ID:
                String movieIdForVideo = uri.getPathSegments().get(2);
                cursor = db.query(FavoriteMovieContract.VideoEntry.TABLE_NAME, projection,
                        FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID + "=?",
                        new String[]{movieIdForVideo}, null, null, sortOrder);
                break;

            case REVIEWS:
                cursor = db.query(FavoriteMovieContract.ReviewEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case REVIEW_WITH_ID:
                String reviewId = uri.getPathSegments().get(1);
                cursor = db.query(FavoriteMovieContract.ReviewEntry.TABLE_NAME, projection,
                        FavoriteMovieContract.ReviewEntry._ID + "=?", new String[]{reviewId},
                        null, null, sortOrder);
                break;

            case VIDEOS:
                cursor = db.query(FavoriteMovieContract.VideoEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case VIDEO_WITH_ID:
                String videoId = uri.getPathSegments().get(1);
                cursor = db.query(FavoriteMovieContract.VideoEntry.TABLE_NAME, projection,
                        FavoriteMovieContract.VideoEntry._ID + "=?", new String[]{videoId},
                        null, null, sortOrder);
                break;

            default:
                throw unsupportedOperationException(uri);
        }

        final Context context = getContext();

        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final String vndCursorDir = "vnd.android.cursor.dir/" + FavoriteMovieContract.AUTHORITY + "/";
        final String vndCursorItem = "vnd.android.cursor.item/" + FavoriteMovieContract.AUTHORITY + "/";

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                return vndCursorDir + FavoriteMovieContract.PATH_MOVIES;
            case MOVIE_WITH_ID:
                return vndCursorItem + FavoriteMovieContract.PATH_MOVIES;
            case REVIEWS_WITH_MOVIE_ID:
                return vndCursorItem + FavoriteMovieContract.PATH_MOVIES + "/" + FavoriteMovieContract.PATH_REVIEWS;
            case VIDEOS_WITH_MOVIE_ID:
                return vndCursorItem + FavoriteMovieContract.PATH_MOVIES + "/" + FavoriteMovieContract.PATH_VIDEOS;

            case REVIEWS:
                return vndCursorDir + FavoriteMovieContract.PATH_REVIEWS;
            case REVIEW_WITH_ID:
                return vndCursorItem + FavoriteMovieContract.PATH_REVIEWS;

            case VIDEOS:
                return vndCursorDir + FavoriteMovieContract.PATH_VIDEOS;
            case VIDEO_WITH_ID:
                return vndCursorItem + FavoriteMovieContract.PATH_VIDEOS;

            default:
                throw unsupportedOperationException(uri);
        }
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        int rowsInserted = 0;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowsInserted += bulkInsert(db, FavoriteMovieContract.MovieEntry.TABLE_NAME, values);
                break;
            case REVIEWS:
                rowsInserted += bulkInsert(db, FavoriteMovieContract.ReviewEntry.TABLE_NAME, values);
                break;
            case VIDEOS:
                rowsInserted += bulkInsert(db, FavoriteMovieContract.VideoEntry.TABLE_NAME, values);
                break;
            case MOVIE_WITH_ID:
            case REVIEWS_WITH_MOVIE_ID:
            case VIDEOS_WITH_MOVIE_ID:
            case REVIEW_WITH_ID:
            case VIDEO_WITH_ID:
            default:
                throw unsupportedOperationException(uri);
        }

        final Context context = getContext();

        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return rowsInserted;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        Uri returnUri = null;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                final long movieId = db.insert(FavoriteMovieContract.MovieEntry.TABLE_NAME, null, values);
                if (movieId > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.MovieEntry.CONTENT_URI, movieId);
                }
                break;

            case REVIEWS:
                final long reviewId = db.insert(FavoriteMovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (reviewId > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.ReviewEntry.CONTENT_URI, reviewId);
                }
                break;

            case VIDEOS:
                final long videoId = db.insert(FavoriteMovieContract.VideoEntry.TABLE_NAME, null, values);
                if (videoId > 0) {
                    returnUri = ContentUris.withAppendedId(FavoriteMovieContract.VideoEntry.CONTENT_URI, videoId);
                }
                break;
            case MOVIE_WITH_ID:
            case REVIEWS_WITH_MOVIE_ID:
            case VIDEOS_WITH_MOVIE_ID:
            case REVIEW_WITH_ID:
            case VIDEO_WITH_ID:
            default:
                throw unsupportedOperationException(uri);
        }

        if (returnUri == null) {
            throw new SQLException("Failed to insert row into " + uri);
        }

        final Context context = getContext();

        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mFavoriteMovieDbHelper.getWritableDatabase();
        String[] movieId = uri.getPathSegments().size() > 1 ? new String[]{uri.getPathSegments().get(1)} : null;
        int rowsDeleted;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                rowsDeleted = db.delete(FavoriteMovieContract.MovieEntry.TABLE_NAME, null, null);
                break;
            case MOVIE_WITH_ID:
                rowsDeleted = db.delete(FavoriteMovieContract.MovieEntry.TABLE_NAME, FavoriteMovieContract.MovieEntry._ID + "=?", movieId);
                break;
            case REVIEWS_WITH_MOVIE_ID:
                movieId = new String[]{uri.getPathSegments().get(2)};
                rowsDeleted = db.delete(FavoriteMovieContract.ReviewEntry.TABLE_NAME, FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + "=?", movieId);
                break;
            case VIDEOS_WITH_MOVIE_ID:
                movieId = new String[]{uri.getPathSegments().get(2)};
                rowsDeleted = db.delete(FavoriteMovieContract.VideoEntry.TABLE_NAME, FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID + "=?", movieId);
                break;

            case REVIEWS:
                rowsDeleted = db.delete(FavoriteMovieContract.ReviewEntry.TABLE_NAME, null, null);
                break;
            case REVIEW_WITH_ID:
                final String[] reviewId = {uri.getPathSegments().get(1)};
                rowsDeleted = db.delete(FavoriteMovieContract.ReviewEntry.TABLE_NAME, FavoriteMovieContract.ReviewEntry._ID + "=?", reviewId);
                break;

            case VIDEOS:
                rowsDeleted = db.delete(FavoriteMovieContract.VideoEntry.TABLE_NAME, null, null);
                break;
            case VIDEO_WITH_ID:
                final String[] videoId = {uri.getPathSegments().get(1)};
                rowsDeleted = db.delete(FavoriteMovieContract.VideoEntry.TABLE_NAME, FavoriteMovieContract.VideoEntry._ID + "=?", videoId);
                break;

            default:
                throw unsupportedOperationException(uri);
        }

        final Context context = getContext();

        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        throw unsupportedOperationException(uri);
    }

    private int bulkInsert(final SQLiteDatabase db, final String tableName, final ContentValues[] values) {
        int rowsInserted = 0;
        db.beginTransaction();

        try {
            for (final ContentValues cv : values) {
                final long inserted = db.insert(tableName, null, cv);
                if (inserted > 0) {
                    rowsInserted++;
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return rowsInserted;
    }

    private UnsupportedOperationException unsupportedOperationException(final Uri uri) {
        return new UnsupportedOperationException("Unknown uri: " + uri);
    }
}
