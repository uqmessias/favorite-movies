package br.com.uilquemessias.favoritemovies.services;

import br.com.uilquemessias.favoritemovies.BuildConfig;
import br.com.uilquemessias.favoritemovies.services.models.MovieResult;
import br.com.uilquemessias.favoritemovies.services.models.ReviewResult;
import br.com.uilquemessias.favoritemovies.services.models.VideoResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class MovieApi {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static MovieApi sInstance;

    private final TheMovieDbService mService;
    private Call<MovieResult> mLastCall;

    private MovieApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mService = retrofit.create(TheMovieDbService.class);
    }

    public static MovieApi instance() {
        if (sInstance == null) {
            sInstance = new MovieApi();
        }

        return sInstance;
    }

    public void getTopRatedMovies(final MovieResultListener movieListener) {
        executeCall(mService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_APP_KEY), movieListener);
    }

    public void getPopularMovies(final MovieResultListener movieListener) {
        executeCall(mService.getPopularMovies(BuildConfig.THE_MOVIE_DB_APP_KEY), movieListener);
    }

    public void getVideosByMovies(final int movieId, final VideoResultListener videoListener) {
        executeCall(mService.getVideosByMovies(movieId, BuildConfig.THE_MOVIE_DB_APP_KEY), videoListener);
    }

    public void getReviewsByMovies(final int movieId, final ReviewResultListener reviewListener) {
        executeCall(mService.getReviewsByMovies(movieId, BuildConfig.THE_MOVIE_DB_APP_KEY), reviewListener);
    }

    private <T> void executeCall(final Call<T> call, final ResultListener<T> listener) {
        if (listener == null) {
            return;
        }

        if (mLastCall != null && !mLastCall.isCanceled() && !mLastCall.isExecuted()) {
            mLastCall.cancel();
        }

        try {
            mLastCall = (Call<MovieResult>) call;
        } finally {
            // the last call doesn't exists
        }

        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    listener.onSuccessResult(response.body());
                } else {
                    listener.onFailure(new RuntimeException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public interface ResultListener<T> {
        void onSuccessResult(T results);

        void onFailure(Throwable exception);
    }

    public interface MovieResultListener extends ResultListener<MovieResult> {
        // listener for Movies
    }

    public interface VideoResultListener extends ResultListener<VideoResult> {
        // listener for Videos
    }

    public interface ReviewResultListener extends ResultListener<ReviewResult> {
        // listener for Reviews
    }
}
