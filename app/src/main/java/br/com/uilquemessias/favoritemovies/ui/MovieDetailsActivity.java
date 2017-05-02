package br.com.uilquemessias.favoritemovies.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.ui.adapters.MoviesAdapter;

public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView mIvBackdrop;
    private ImageView mIvPoster;
    private TextView mTvTitle;
    private TextView mTvReleaseDate;
    private TextView mTvOverview;
    private TextView mTvVoteAverage;
    private Picasso mPicasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        bindViews();
    }

    @Override
    protected void onDestroy() {
        if (mPicasso != null) {
            mPicasso.cancelRequest(mIvPoster);
            mPicasso.cancelRequest(mIvBackdrop);
            mPicasso = null;
        }

        super.onDestroy();
    }

    private void bindViews() {
        mIvBackdrop = findView(R.id.iv_movie_backdrop);
        mIvPoster = findView(R.id.iv_movie_poster);
        mTvTitle = findView(R.id.tv_movie_title);
        mTvReleaseDate = findView(R.id.tv_movie_release_date);
        mTvVoteAverage = findView(R.id.tv_movie_vote_average);
        mTvOverview = findView(R.id.tv_movie_overview);

        if (getIntent().hasExtra(MovieListActivity.SELECTED_MOVIE)) {
            final Movie movie = getIntent().getParcelableExtra(MovieListActivity.SELECTED_MOVIE);

            if (movie == null) {
                return;
            }

            final Uri urlPoster = Uri.parse(MoviesAdapter.BASE_IMAGE_URL + movie.getPosterPath());
            final Uri urlBackdrop = Uri.parse(MoviesAdapter.BASE_IMAGE_LARGER_URL + movie.getBackdropPath());

            mPicasso = Picasso.with(this);
            mPicasso.load(urlPoster)
                    .placeholder(R.drawable.movie_poster)
                    .into(mIvPoster);
            mPicasso.load(urlBackdrop)
                    .placeholder(R.drawable.movie_backdrop)
                    .into(mIvBackdrop);

            mTvTitle.setText(movie.getTitle());
            mTvReleaseDate.setText(movie.getReleaseDate());
            mTvVoteAverage.setText(String.format("%.2f", movie.getVoteAverage()));
            mTvOverview.setText(movie.getOverview());
        }
    }

    private <T> T findView(@IdRes int id) {
        return (T) findViewById(id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
