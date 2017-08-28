package br.com.uilquemessias.favoritemovies.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.MovieApi;
import br.com.uilquemessias.favoritemovies.services.favorites.FavoriteManager;
import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.ReviewResult;
import br.com.uilquemessias.favoritemovies.services.models.Video;
import br.com.uilquemessias.favoritemovies.services.models.VideoResult;
import br.com.uilquemessias.favoritemovies.ui.adapters.MoviesAdapter;
import br.com.uilquemessias.favoritemovies.ui.adapters.ReviewsAdapter;
import br.com.uilquemessias.favoritemovies.ui.adapters.VideosAdapter;
import br.com.uilquemessias.favoritemovies.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MovieDetailsActivity extends AppCompatActivity implements VideosAdapter.ListItemClickListener {

    private static final String TAG = "MovieDetailsActivity";

    @BindView(R.id.iv_movie_backdrop)
    ImageView mIvBackdrop;
    @BindView(R.id.iv_movie_poster)
    ImageView mIvPoster;
    @BindView(R.id.tv_movie_title)
    TextView mTvTitle;
    @BindView(R.id.tv_movie_release_date)
    TextView mTvReleaseDate;
    @BindView(R.id.tv_movie_overview)
    TextView mTvOverview;
    @BindView(R.id.tv_movie_vote_average)
    TextView mTvVoteAverage;
    @BindView(R.id.rv_video_list)
    RecyclerView mRvVideoList;
    @BindView(R.id.rv_review_list)
    RecyclerView mRvReviewList;
    @BindView(R.id.tv_there_is_no_video)
    TextView mTvThereIsNoVideo;
    @BindView(R.id.tv_there_is_no_review)
    TextView mTvThereIsNoReview;
    @BindView(R.id.pb_video_loading)
    ProgressBar mPbVideoLoading;
    @BindView(R.id.pb_review_loading)
    ProgressBar mPbReviewLoading;
    @BindView(R.id.im_movie_favorite)
    ImageView imMovieFavorite;

    private Movie mMovie;

    private VideosAdapter mVideosAdapter;
    private ReviewsAdapter mReviewsAdapter;
    private FavoriteManager mFavoriteManager;

    private Picasso mPicasso;
    private Unbinder mUnbinder;
    private MovieApi.VideoResultListener mVideoListener = new MovieApi.VideoResultListener() {
        @Override
        public void onSuccessResult(VideoResult results) {
            final List<String> videoList = new ArrayList<>();

            for (final Video video : results.getVideos()) {
                if (Video.SITE_YOUTUBE.equalsIgnoreCase(video.getSite())) {
                    videoList.add(video.getKey());
                }
            }

            mVideosAdapter.setVideos(videoList);

            if (videoList.isEmpty()) {
                showNoVideos();
            } else {
                showVideos();
            }
        }

        @Override
        public void onFailure(Throwable exception) {
            showNoVideos();
        }
    };
    private MovieApi.ReviewResultListener mReviewListener = new MovieApi.ReviewResultListener() {
        @Override
        public void onSuccessResult(ReviewResult results) {
            mReviewsAdapter.setReviews(results.getReviews());
            if (results.getReviews().isEmpty()) {
                showNoReviews();
            } else {
                showReviews();
            }
        }

        @Override
        public void onFailure(Throwable exception) {
            showNoReviews();
        }
    };

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

        mUnbinder = ButterKnife.bind(this);

        if (getIntent().hasExtra(MovieListActivity.SELECTED_MOVIE)) {
            mMovie = getIntent().getParcelableExtra(MovieListActivity.SELECTED_MOVIE);
        }


        if (mMovie == null) {
            onBackPressed();
            return;
        }

        mFavoriteManager = new FavoriteManager(this);

        bindViews();

        mVideosAdapter = new VideosAdapter(this);
        mRvVideoList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvVideoList.setHasFixedSize(true);
        mRvVideoList.setAdapter(mVideosAdapter);
        tryShowVideos();

        mReviewsAdapter = new ReviewsAdapter();
        mRvReviewList.setLayoutManager(new LinearLayoutManager(this));
        mRvReviewList.setHasFixedSize(true);
        mRvReviewList.setAdapter(mReviewsAdapter);
        tryShowReviews();
    }

    @Override
    protected void onDestroy() {
        if (mPicasso != null) {
            mPicasso.cancelRequest(mIvPoster);
            mPicasso.cancelRequest(mIvBackdrop);
            mPicasso = null;
        }

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }

        if (mFavoriteManager != null) {
            mFavoriteManager.close();
        }

        super.onDestroy();
    }

    private void bindViews() {
        final Uri urlPoster = Uri.parse(MoviesAdapter.BASE_IMAGE_URL + mMovie.getPosterPath());
        final Uri urlBackdrop = Uri.parse(MoviesAdapter.BASE_IMAGE_LARGER_URL + mMovie.getBackdropPath());
        final boolean isFavorite = mFavoriteManager.containsMovieWithId(mMovie.getId());

        mPicasso = Picasso.with(this);
        mPicasso.load(urlPoster)
                .placeholder(R.drawable.movie_poster)
                .into(mIvPoster);
        mPicasso.load(urlBackdrop)
                .placeholder(R.drawable.movie_backdrop)
                .into(mIvBackdrop);

        mTvTitle.setText(mMovie.getTitle());
        mTvReleaseDate.setText(mMovie.getReleaseDate());
        mTvVoteAverage.setText(String.format(Locale.US, "%.2f", mMovie.getVoteAverage()));
        mTvOverview.setText(mMovie.getOverview());
        imMovieFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite_on : R.drawable.ic_favorite_off);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(String key, Uri videoUri) {
        Log.d(TAG, "key: " + key + " - videoUri: " + videoUri);
        final Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, videoUri);
        startActivity(youtubeIntent);
    }

    @OnClick(R.id.im_movie_favorite)
    void onFavoriteClick(final ImageView imMovieFavorite) {
        final boolean isFavorite = mFavoriteManager.containsMovieWithId(mMovie.getId());

        if (isFavorite) {
            mFavoriteManager.removeMovie(mMovie.getId());
            mFavoriteManager.removeReviews(mMovie.getId());
            mFavoriteManager.removeVideos(mMovie.getId());
            imMovieFavorite.setImageResource(R.drawable.ic_favorite_off);

            Toast.makeText(this, "Movie removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            mFavoriteManager.putMovie(mMovie);
            mFavoriteManager.putReviews(mMovie.getId(), mReviewsAdapter.getReviews());
            mFavoriteManager.putVideos(mMovie.getId(), mVideosAdapter.getVideos());
            imMovieFavorite.setImageResource(R.drawable.ic_favorite_on);

            Toast.makeText(this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    private void tryShowVideos() {
        showLoadingVideos();
        MovieApi.instance().getVideosByMovies(mMovie.getId(), mVideoListener);
    }

    private void showVideos() {
        ViewUtils.getInstance()
                .gone(mPbVideoLoading, mTvThereIsNoVideo)
                .visible(mRvVideoList);
        Log.d(TAG, "Showing videos");
    }

    private void showNoVideos() {
        ViewUtils.getInstance()
                .gone(mPbVideoLoading, mRvVideoList)
                .visible(mTvThereIsNoVideo);
        Log.d(TAG, "There's no reviews to be showed");
    }

    private void showLoadingVideos() {
        ViewUtils.getInstance()
                .gone(mTvThereIsNoVideo, mRvVideoList)
                .visible(mPbVideoLoading);
        Log.d(TAG, "loading videos");
    }

    private void tryShowReviews() {
        showLoadingReviews();
        MovieApi.instance().getReviewsByMovies(mMovie.getId(), mReviewListener);
    }

    private void showReviews() {
        ViewUtils.getInstance()
                .gone(mPbReviewLoading, mTvThereIsNoReview)
                .visible(mRvReviewList);
        Log.d(TAG, "Showing reviews");
    }

    private void showNoReviews() {
        ViewUtils.getInstance()
                .gone(mPbReviewLoading, mRvReviewList)
                .visible(mTvThereIsNoReview);
        Log.d(TAG, "There's no reviews to be showed");
    }

    private void showLoadingReviews() {
        ViewUtils.getInstance()
                .gone(mTvThereIsNoReview, mRvReviewList)
                .visible(mPbReviewLoading);
        Log.d(TAG, "loading reviews");
    }
}
