package br.com.uilquemessias.favoritemovies.services.favorites;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class FavoriteMovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_DROP_IF_EXISTS = "DROP TABLE IF EXISTS ";
    private static final String SQL_CREATE = "CREATE TABLE ";

    private final String SQL_CREATE_MOVIE_TABLE = SQL_CREATE +
            FavoriteMovieContract.MovieEntry.TABLE_NAME + " (" +
            FavoriteMovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_TITLE + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_OVERVIEW + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_BACKDROP_PATH + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_POSTER_PATH + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_RELEASE_DATE + " TEXT NOT NULL," +
            FavoriteMovieContract.MovieEntry.COLUMN_NAME_VOTE_AVERAGE + " REAL NOT NULL);";

    private final String SQL_CREATE_REVIEW_TABLE = SQL_CREATE +
            FavoriteMovieContract.ReviewEntry.TABLE_NAME + " (" +
            FavoriteMovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteMovieContract.ReviewEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            FavoriteMovieContract.ReviewEntry.COLUMN_NAME_AUTHOR + " TEXT NOT NULL," +
            FavoriteMovieContract.ReviewEntry.COLUMN_NAME_CONTENT + " TEXT NOT NULL);";

    private final String SQL_CREATE_VIDEO_TABLE = SQL_CREATE +
            FavoriteMovieContract.VideoEntry.TABLE_NAME + " (" +
            FavoriteMovieContract.VideoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            FavoriteMovieContract.VideoEntry.COLUMN_NAME_MOVIE_ID + " INTEGER NOT NULL," +
            FavoriteMovieContract.VideoEntry.COLUMN_NAME_KEY + " TEXT NOT NULL);";

    private static final String SQL_DROP_MOVIE_TABLE = SQL_DROP_IF_EXISTS + FavoriteMovieContract.MovieEntry.TABLE_NAME;
    private static final String SQL_DROP_REVIEW_TABLE = SQL_DROP_IF_EXISTS + FavoriteMovieContract.ReviewEntry.TABLE_NAME;
    private static final String SQL_DROP_VIDEO_TABLE = SQL_DROP_IF_EXISTS + FavoriteMovieContract.VideoEntry.TABLE_NAME;

    FavoriteMovieDbHelper(Context context) {
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
}
