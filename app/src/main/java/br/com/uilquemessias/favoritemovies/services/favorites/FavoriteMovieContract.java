package br.com.uilquemessias.favoritemovies.services.favorites;

import android.provider.BaseColumns;

final class FavoriteMovieContract {
    private FavoriteMovieContract() {
        // nothing
    }

    static class MovieEntry implements BaseColumns {
        static final String TABLE_NAME = "movie";

        static final String COLUMN_NAME_TITLE = "title";
        static final String COLUMN_NAME_OVERVIEW = "overview";
        static final String COLUMN_NAME_BACKDROP_PATH = "backdrop_path";
        static final String COLUMN_NAME_POSTER_PATH = "poster_path";
        static final String COLUMN_NAME_RELEASE_DATE = "release_date";
        static final String COLUMN_NAME_VOTE_AVERAGE = "vote_average";
    }

    static class ReviewEntry implements BaseColumns {
        static final String TABLE_NAME = "review";

        static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        static final String COLUMN_NAME_AUTHOR = "author";
        static final String COLUMN_NAME_CONTENT = "content";
    }

    static class VideoEntry implements BaseColumns {
        static final String TABLE_NAME = "video";

        static final String COLUMN_NAME_MOVIE_ID = "movie_id";
        static final String COLUMN_NAME_KEY = "key";
    }
}
