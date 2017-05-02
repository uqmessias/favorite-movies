package br.com.uilquemessias.favoritemovies.ui;

import android.os.Bundle;
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
import android.widget.Toast;

import java.util.ArrayList;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.MovieApi;
import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.MovieResult;
import br.com.uilquemessias.favoritemovies.ui.adapters.MoviesAdapter;
import br.com.uilquemessias.favoritemovies.utils.ViewUtils;

public class MovieListActivity extends AppCompatActivity implements MovieApi.MovieResultListener, MoviesAdapter.ListItemClickListener {

    private static final String SPINNER_ITEM_TOP_RATED = "Top rated";
    private static final String SPINNER_ITEM_MOST_POPULAR = "Most popular";
    private static final String TAG = "MovieListActivity";

    private ProgressBar mPbLoading;
    private TextView mTvError;
    private TextView mTvEmpty;
    private RecyclerView mRvMovieList;
    private MoviesAdapter mMoviesAdapter;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        mTvError = (TextView) findViewById(R.id.tv_error);
        mTvEmpty = (TextView) findViewById(R.id.tv_empty);
        mRvMovieList = (RecyclerView) findViewById(R.id.rv_movie_list);

        final int colSpan = getResources().getInteger(R.integer.col_span);
        mMoviesAdapter = new MoviesAdapter(this);
        mRvMovieList.setLayoutManager(new GridLayoutManager(this, colSpan));
        mRvMovieList.setHasFixedSize(true);
        mRvMovieList.setAdapter(mMoviesAdapter);
        mRvMovieList.addItemDecoration(new MoviesAdapter.GridSpacingItemDecoration(colSpan, 20, true));

        Spinner spinnerOrderBy = (Spinner) findViewById(R.id.sp_order_by);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                new String[]{SPINNER_ITEM_TOP_RATED, SPINNER_ITEM_MOST_POPULAR}
        );
        spinnerOrderBy.setAdapter(spinnerAdapter);
        spinnerOrderBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                }

                Log.d(TAG, "wrong selection!");
                showError();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "nothing selected!");
            }
        });
    }

    private void tryShowTopRated() {
        showLoading();
        MovieApi.instance().getTopRatedMovies(this);
    }

    private void tryShowMostPopular() {
        showLoading();
        MovieApi.instance().getPopularMovies(this);
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
    public void onMovieResult(MovieResult movies) {
        if (movies == null || movies.getMovies() == null) {
            showError();
            return;
        }

        Log.d(TAG, "total movies: " + movies.getTotalResults());
        if (movies.getTotalResults() >= 1) {
            mMoviesAdapter.setMovies(movies.getMovies());
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
        String str = String.format("the movie '%s' launched at '%s' (image: '%s') rated with %f.2 and synopsis:\n %s",
                movie.getTitle(), movie.getReleaseDate(),
                movie.getPosterPath(), movie.getVoteAverage(),
                movie.getOverview());
       
        if (mToast != null) {
            mToast.cancel();
        }

        mToast = Toast.makeText(this, str, Toast.LENGTH_LONG);
        mToast.show();
    }
}
