package br.com.uilquemessias.favoritemovies.services;

import br.com.uilquemessias.favoritemovies.BuildConfig;
import br.com.uilquemessias.favoritemovies.services.models.MovieResult;
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

    private void executeCall(final Call<MovieResult> call, final MovieResultListener movieListener) {
        if (movieListener == null) {
            return;
        }

        if (mLastCall != null && !mLastCall.isCanceled() && !mLastCall.isExecuted()) {
            mLastCall.cancel();
        }

        mLastCall = call;

        mLastCall.enqueue(new Callback<MovieResult>() {
            @Override
            public void onResponse(Call<MovieResult> call, Response<MovieResult> response) {
                if (response.isSuccessful()) {
                    movieListener.onMovieResult(response.body());
                } else {
                    movieListener.onFailure(new RuntimeException(response.message()));
                }
            }

            @Override
            public void onFailure(Call<MovieResult> call, Throwable t) {
                movieListener.onFailure(t);
            }
        });

    }

    public interface MovieResultListener {

        void onMovieResult(MovieResult movies);

        void onFailure(Throwable exception);
    }
}
