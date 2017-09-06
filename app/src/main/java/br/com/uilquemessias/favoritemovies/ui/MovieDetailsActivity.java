package br.com.uilquemessias.favoritemovies.ui;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import java.util.Locale;

import br.com.uilquemessias.favoritemovies.R;
import br.com.uilquemessias.favoritemovies.services.MovieApi;
import br.com.uilquemessias.favoritemovies.services.favorites.FavoriteHelper;
import br.com.uilquemessias.favoritemovies.services.favorites.FavoriteMovieContract;
import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Review;
import br.com.uilquemessias.favoritemovies.services.models.Video;
import br.com.uilquemessias.favoritemovies.ui.adapters.MoviesAdapter;
import br.com.uilquemessias.favoritemovies.ui.adapters.ReviewsAdapter;
import br.com.uilquemessias.favoritemovies.ui.adapters.VideosAdapter;
import br.com.uilquemessias.favoritemovies.utils.ViewUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MovieDetailsActivity extends AppCompatActivity implements VideosAdapter.ListItemClickListener,
        LoaderManager.LoaderCallbacks<Boolean> {

    private static final String TAG = "MovieDetailsActivity";
    private static final int LOADER_TOGGLE_FAVORITE_ID = 100;
    private static final int LOADER_CHECK_FAVORITE_ID = 101;
    private static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";
    private static final String MOVIE_KEY = "MOVIE_KEY";
    private static final String REVIEWS_KEY = "REVIEWS_KEY";
    private static final String VIDEOS_KEY = "VIDEOS_KEY";

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

    private Picasso mPicasso;
    private Unbinder mUnbinder;
    private MovieApi.VideoResultListener mVideoListener = new MovieApi.VideoResultListener() {
        @Override
        public void onSuccessResult(final ArrayList<Video> videos, int totalVideos, int totalPages) {
            final ArrayList<String> videoList = new ArrayList<>();

            if (videos != null) {
                for (final Video video : videos) {
                    if (Video.SITE_YOUTUBE.equalsIgnoreCase(video.getSite())) {
                        videoList.add(video.getKey());
                    }
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
        public void onSuccessResult(final ArrayList<Review> reviews, int totalVideos, int totalPages) {
            mReviewsAdapter.setReviews(reviews);
            if (reviews != null && reviews.isEmpty()) {
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

    private boolean mIsFavorite = false;

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

        final Bundle args = new Bundle();
        args.putInt(MOVIE_ID_KEY, mMovie.getId());
        getSupportLoaderManager().restartLoader(LOADER_CHECK_FAVORITE_ID, args, this);

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

        super.onDestroy();
    }

    private void bindViews() {
        final Uri urlPoster = Uri.parse(MoviesAdapter.BASE_IMAGE_URL + mMovie.getPosterPath());
        final Uri urlBackdrop = Uri.parse(MoviesAdapter.BASE_IMAGE_LARGER_URL + mMovie.getBackdropPath());

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
    void onFavoriteClick() {
        final Bundle args = new Bundle();
        args.putParcelable(MOVIE_KEY, mMovie);
        args.putStringArrayList(VIDEOS_KEY, mVideosAdapter.getVideos());
        args.putParcelableArrayList(REVIEWS_KEY, mReviewsAdapter.getReviews());
        getSupportLoaderManager().restartLoader(LOADER_TOGGLE_FAVORITE_ID, args, this);
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

    private static final String tags = "Loading";

    @Override
    public Loader<Boolean> onCreateLoader(int id, final Bundle args) {
        switch (id) {
            case LOADER_CHECK_FAVORITE_ID:

                if (!args.containsKey(MOVIE_ID_KEY)) {
                    throw new IllegalArgumentException("You have to pass the MOVIE_ID_KEY as an argument.");
                }

                return new AsyncTaskLoader<Boolean>(this) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Boolean loadInBackground() {
                        Cursor cursor = null;

                        try {
                            String[] selectionArgs = new String[]{String.valueOf(args.getInt(MOVIE_ID_KEY))};
                            cursor = getContentResolver()
                                    .query(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                            new String[]{FavoriteMovieContract.MovieEntry._ID},
                                            FavoriteMovieContract.MovieEntry._ID + "=?",
                                            selectionArgs, null);
                            return cursor != null && cursor.getCount() > 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                };
            case LOADER_TOGGLE_FAVORITE_ID:

                if (!args.containsKey(MOVIE_KEY) || !args.containsKey(REVIEWS_KEY) || !args.containsKey(VIDEOS_KEY)) {
                    throw new IllegalArgumentException("You have to pass the MOVIE_KEY, REVIEWS_KEY and VIDEOS_KEY as arguments.");
                }
                Log.d(tags, "antes de criar");

                return new AsyncTaskLoader<Boolean>(this) {
                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();
                        forceLoad();
                    }

                    @Override
                    public Boolean loadInBackground() {
                        Cursor cursor = null;
                        Log.d(tags, "loadInBackground");

                        try {
                            final ContentResolver cr = getContentResolver();
                            final Movie movie = args.getParcelable(MOVIE_KEY);
                            final int movieId = movie.getId();
                            String[] selectionArgs = new String[]{String.valueOf(movieId)};
                            cursor = cr.query(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                    new String[]{FavoriteMovieContract.MovieEntry._ID},
                                    FavoriteMovieContract.MovieEntry._ID + "=?",
                                    selectionArgs, null
                            );
                            if (cursor != null && cursor.getCount() > 0) {
                                Uri movieWIthId = FavoriteMovieContract.MovieEntry.getMovieById(movieId);
                                Uri reviewsWithMovieId = FavoriteMovieContract.MovieEntry.getReviewsByMovieContentUri(movieId);
                                Uri videosWithMovieId = FavoriteMovieContract.MovieEntry.getVideosByMovieContentUri(movieId);
                                Log.d(tags, "deletado");
                                int rowsDeletede = cr.delete(movieWIthId, null, null) +
                                        cr.delete(reviewsWithMovieId, null, null) +
                                        cr.delete(videosWithMovieId, null, null);

                                return rowsDeletede == 0;
                            } else {
                                final ArrayList<Review> reviews = args.getParcelableArrayList(REVIEWS_KEY);
                                final ArrayList<String> videos = args.getStringArrayList(VIDEOS_KEY);
                                Log.d(tags, "antes de inserir");
                                // inserting the movie
                                cr.insert(FavoriteMovieContract.MovieEntry.CONTENT_URI,
                                        FavoriteHelper.getContentValuesFromMovie(movie));
                                Log.d(tags, "depois de inserir");

                                // inserting the reviews
                                if (reviews != null && !reviews.isEmpty()) {
                                    final ContentValues[] reviewValues = FavoriteHelper.getContentsValuesFromReviews(
                                            movieId, reviews
                                    );

                                    cr.bulkInsert(FavoriteMovieContract.ReviewEntry.CONTENT_URI,
                                            reviewValues);
                                    Log.d(tags, "depois de inserir reviews");
                                }

                                // inserting the videos
                                if (videos != null && !videos.isEmpty()) {
                                    final ContentValues[] videoValues = FavoriteHelper.getContentsValuesFromVideos(
                                            movieId, videos
                                    );

                                    cr.bulkInsert(FavoriteMovieContract.VideoEntry.CONTENT_URI,
                                            videoValues);
                                    Log.d(tags, "depois de inserir videos");
                                }

                                return true;
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Log.d(tags, "erro muito louco", ex);
                            return false;
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                };
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean isFavorite) {
        Log.d(tags, "onLoadFinished: " + isFavorite);
        if (isFavorite) {
            imMovieFavorite.setImageResource(R.drawable.ic_favorite_on);

            if (loader.getId() == LOADER_TOGGLE_FAVORITE_ID) {
                Toast.makeText(this, "Movie added to favorites", Toast.LENGTH_SHORT).show();
            }
        } else {
            imMovieFavorite.setImageResource(R.drawable.ic_favorite_off);

            if (loader.getId() == LOADER_TOGGLE_FAVORITE_ID) {
                Toast.makeText(this, "Movie removed from favorites", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }
}
