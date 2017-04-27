package br.com.uilquemessias.favoritemovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import br.com.uilquemessias.favoritemovies.services.MovieApi;
import br.com.uilquemessias.favoritemovies.services.models.MovieResult;

public class MovieListActivity extends AppCompatActivity implements MovieApi.MovieResultListener {

    private static final String SPINNER_ITEM_TOP_RATED = "Top rated";
    private static final String SPINNER_ITEM_MOST_POPULAR = "Most popular";
    private static final String TAG = "MovieListActivity";

    private TextView mTvError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTvError = (TextView) findViewById(R.id.tv_error);

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
        MovieApi.instance().getTopRatedMovies(this);
    }

    private void tryShowMostPopular() {
        MovieApi.instance().getPopularMovies(this);
    }

    private void showMovies() {
        // do something
        Log.d(TAG, "success!");
    }

    private void showError() {
        mTvError.setVisibility(View.VISIBLE);
        Log.d(TAG, "something went wrong!");
    }

    @Override
    public void onMovieResult(MovieResult movies) {
        Log.d(TAG, "total movies: " + movies.getTotalResults());
        showMovies();
    }

    @Override
    public void onFailure(Throwable exception) {
        Log.d(TAG, "something went wrong!", exception);
        showError();
    }
}
