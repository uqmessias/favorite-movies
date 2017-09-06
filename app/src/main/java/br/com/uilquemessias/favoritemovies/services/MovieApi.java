package br.com.uilquemessias.favoritemovies.services;

import java.util.ArrayList;

import br.com.uilquemessias.favoritemovies.BuildConfig;
import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Result;
import br.com.uilquemessias.favoritemovies.services.models.Review;
import br.com.uilquemessias.favoritemovies.services.models.Video;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class MovieApi {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static MovieApi sInstance;

    private final TheMovieDbService mService;
    private Call<Result<Movie>> mLastCall;

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

    private Call<Result<Movie>> tryToCancelLastCall(final Call<Result<Movie>> lastCall, Call<Result<Movie>> call) {
        if (lastCall != null && !lastCall.isCanceled() && !lastCall.isExecuted()) {
            lastCall.cancel();
        }

        return call;
    }

    public void getTopRatedMovies(final MovieResultListener movieListener) {
        mLastCall = tryToCancelLastCall(mLastCall, mService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_APP_KEY));
        executeCall(mLastCall, movieListener);
    }

    public void getPopularMovies(final MovieResultListener movieListener) {
        mLastCall = tryToCancelLastCall(mLastCall, mService.getPopularMovies(BuildConfig.THE_MOVIE_DB_APP_KEY));
        executeCall(mLastCall, movieListener);
    }

    public void getVideosByMovies(final int movieId, final VideoResultListener videoListener) {
        executeCall(mService.getVideosByMovies(movieId, BuildConfig.THE_MOVIE_DB_APP_KEY), videoListener);
    }

    public void getReviewsByMovies(final int movieId, final ReviewResultListener reviewListener) {
        executeCall(mService.getReviewsByMovies(movieId, BuildConfig.THE_MOVIE_DB_APP_KEY), reviewListener);
    }

    private <T> void executeCall(final Call<Result<T>> call, final ResultListener<T> listener) {
        if (listener == null) {
            return;
        }

        call.enqueue(new Callback<Result<T>>() {
            @Override
            public void onResponse(Call<Result<T>> call, Response<Result<T>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    final Result<T> result = response.body();
                    listener.onSuccessResult(result.getResults(), result.getTotalResults(), result.getTotalPages());
                } else {
                    listener.onFailure(new RuntimeException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<Result<T>> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public interface ResultListener<T> {
        void onSuccessResult(ArrayList<T> results, int totalResults, int totalPages);

        void onFailure(Throwable exception);
    }

    public interface MovieResultListener extends ResultListener<Movie> {
        // listener for Movies
    }

    public interface VideoResultListener extends ResultListener<Video> {
        // listener for Videos
    }

    public interface ReviewResultListener extends ResultListener<Review> {
        // listener for Reviews
    }
}
