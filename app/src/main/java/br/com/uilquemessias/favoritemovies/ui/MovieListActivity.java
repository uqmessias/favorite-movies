package br.com.uilquemessias.favoritemovies.ui;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.MovieApi;
import br.com.uilquemessias.favoritemovies.services.favorites.FavoriteHelper;
import br.com.uilquemessias.favoritemovies.services.favorites.FavoriteMovieContract;
import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.ui.adapters.GridSpacingItemDecoration;
import br.com.uilquemessias.favoritemovies.ui.adapters.MoviesAdapter;
import br.com.uilquemessias.favoritemovies.utils.ViewUtils;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MovieListActivity extends AppCompatActivity implements MovieApi.MovieResultListener, MoviesAdapter.ListItemClickListener, LoaderManager.LoaderCallbacks<ArrayList<Movie>> {

    private static final String SPINNER_ITEM_TOP_RATED = "Top rated";
    private static final String SPINNER_ITEM_MOST_POPULAR = "Most popular";
    private static final String SPINNER_ITEM_FAVORITES = "Favorites";
    private static final String TAG = "MovieListActivity";
    private static final String MOVIES = "MOVIES";
    private static final String SELECTED_ORDER = "SELECTED_ORDER";
    private static final int LIST_FAVORITES_LOADER_ID = 26;

    public static final String SELECTED_MOVIE = "SELECTED_MOVIE";

    @BindView(R.id.sp_order_by)
    Spinner mSpinnerOrderBy;
    @BindView(R.id.pb_loading)
    ProgressBar mPbLoading;
    @BindView(R.id.tv_error)
    TextView mTvError;
    @BindView(R.id.tv_empty)
    TextView mTvEmpty;
    @BindView(R.id.rv_movie_list)
    RecyclerView mRvMovieList;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindInt(R.integer.col_span)
    int mColSpan;

    private ArrayAdapter<String> mSpinnerAdapter;

    private MoviesAdapter mMoviesAdapter;
    private boolean mIsFirstSelection = true;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        mUnbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mSpinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{SPINNER_ITEM_TOP_RATED, SPINNER_ITEM_MOST_POPULAR, SPINNER_ITEM_FAVORITES}
        );
        mSpinnerOrderBy.setAdapter(mSpinnerAdapter);
        mSpinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mIsFirstSelection) {
                    Log.d(TAG, "passed out the first time");
                    mIsFirstSelection = false;
                    return;
                }

                String itemSelected = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(itemSelected)) {
                    if (itemSelected.equals(SPINNER_ITEM_TOP_RATED)) {
                        Log.d(TAG, "top rated selected");
                        tryShowTopRated();
                        return;
                    }

                    if (itemSelected.equals(SPINNER_ITEM_MOST_POPULAR)) {
                        Log.d(TAG, "most popular selected");
                        tryShowMostPopular();
                        return;
                    }

                    if (itemSelected.equals(SPINNER_ITEM_FAVORITES)) {
                        Log.d(TAG, "favorites selected");
                        tryShowFavorites();
                        return;
                    }
                }

                Log.d(TAG, "wrong selection!");
                showError();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "nothing selected!");
            }
        });

        int orderSelectionPosition = 0;

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_ORDER)) {
            orderSelectionPosition = savedInstanceState.getInt(SELECTED_ORDER);
            Log.d(TAG, "order saved in position: " + orderSelectionPosition);
        }

        if (savedInstanceState == null || !savedInstanceState.containsKey(MOVIES + orderSelectionPosition)) {
            mMoviesAdapter = new MoviesAdapter(this);
            Log.d(TAG, "before select order by position");
            mSpinnerOrderBy.setSelection(orderSelectionPosition);
            mIsFirstSelection = false;
        } else {
            final ArrayList<Movie> movies = savedInstanceState.getParcelableArrayList(MOVIES + orderSelectionPosition);
            Log.d(TAG, movies.size() + " items restored from bundle");

            mMoviesAdapter = new MoviesAdapter(this, movies);
        }

        mRvMovieList.setLayoutManager(new GridLayoutManager(this, mColSpan));
        mRvMovieList.setHasFixedSize(true);
        mRvMovieList.addItemDecoration(new GridSpacingItemDecoration(mColSpan, 20, true));
        mRvMovieList.setAdapter(mMoviesAdapter);
    }

    @Override
    protected void onDestroy() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        final int positionOrder = mSpinnerOrderBy.getSelectedItemPosition();
        Log.d(TAG, "save order position: " + positionOrder);
        outState.putInt(SELECTED_ORDER, positionOrder);
        outState.putParcelableArrayList(MOVIES + positionOrder, (ArrayList<Movie>) mMoviesAdapter.getMovies());
        super.onSaveInstanceState(outState);
    }

    private void tryShowTopRated() {
        showLoading();
        MovieApi.instance().getTopRatedMovies(this);
    }

    private void tryShowMostPopular() {
        showLoading();
        MovieApi.instance().getPopularMovies(this);
    }

    private void tryShowFavorites() {
        getSupportLoaderManager().restartLoader(LIST_FAVORITES_LOADER_ID, new Bundle(), this);
    }

    private void showMovies() {
        // do something
        ViewUtils.getInstance()
                .gone(mPbLoading, mTvError, mTvEmpty)
                .visible(mRvMovieList);
        Log.d(TAG, "success!");
    }

    private void showEmpty() {
        // do something
        ViewUtils.getInstance()
                .gone(mPbLoading, mTvError, mRvMovieList)
                .visible(mTvEmpty);
        Log.d(TAG, "empty!");
    }

    private void showError() {
        ViewUtils.getInstance()
                .gone(mPbLoading, mRvMovieList, mTvEmpty)
                .visible(mTvError);
        Log.d(TAG, "something went wrong!");
    }

    private void showLoading() {
        ViewUtils.getInstance()
                .gone(mTvError, mRvMovieList, mTvEmpty)
                .visible(mPbLoading);
        Log.d(TAG, "loading...");
    }

    @Override
    public void onSuccessResult(ArrayList<Movie> movies, int totalMovies, int totalPages) {
        if (movies == null) {
            showError();
            return;
        }

        if (totalMovies >= 1) {
            mMoviesAdapter.setMovies(movies);
            showMovies();
            return;
        }

        mMoviesAdapter.setMovies(new ArrayList<Movie>());
        showEmpty();
    }

    @Override
    public void onFailure(Throwable exception) {
        Log.d(TAG, "something went wrong!", exception);
        showError();
    }

    @Override
    public void onListItemClick(Movie movie) {

        String str = movie == null ? "No movie data" : String.format(Locale.US, "the movie '%s' launched at '%s' (poster: '%s' and backdrop: '%s') rated with %.2f and synopsis:\n %s",
                movie.getTitle(), movie.getReleaseDate(),
                movie.getPosterPath(), movie.getBackdropPath(),
                movie.getVoteAverage(), movie.getOverview());

        Log.d(TAG, str);
        final Intent movieIntent = new Intent(this, MovieDetailsActivity.class);
        movieIntent.putExtra(SELECTED_MOVIE, movie);
        startActivity(movieIntent);
    }

    @Override
    public Loader<ArrayList<Movie>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Movie>>(this) {
            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                final String itemSelected = (String) (mSpinnerOrderBy != null ? mSpinnerOrderBy.getSelectedItem() : null);

                if (!TextUtils.isEmpty(itemSelected) && itemSelected.equals(SPINNER_ITEM_FAVORITES)) {
                    showLoading();
                    forceLoad();
                }
            }

            @Override
            public ArrayList<Movie> loadInBackground() {
                final ArrayList<Movie> favorites = new ArrayList<>();
                Cursor cursor = null;

                try {
                    cursor = getContentResolver()
                            .query(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                    null, null, null, null);

                    if (cursor != null) {
                        while (cursor.moveToNext()) {
                            final Movie movie = FavoriteHelper.getMovieFromCursor(cursor);

                            if (movie != null) {
                                favorites.add(movie);
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                return favorites;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Movie>> loader, ArrayList<Movie> movies) {
        if (!movies.isEmpty()) {
            mMoviesAdapter.setMovies(movies);
            showMovies();
        } else {
            showEmpty();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Movie>> loader) {
        // do nothing
    }
}
