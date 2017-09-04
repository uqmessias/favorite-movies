package br.com.uilquemessias.favoritemovies.services;

import br.com.uilquemessias.favoritemovies.services.models.Movie;
import br.com.uilquemessias.favoritemovies.services.models.Result;
import br.com.uilquemessias.favoritemovies.services.models.Review;
import br.com.uilquemessias.favoritemovies.services.models.Video;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbService {
    @GET("movie/top_rated")
    Call<Result<Movie>> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<Result<Movie>> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{movieId}/reviews")
    Call<Result<Review>> getReviewsByMovies(@Path("movieId") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movieId}/videos")
    Call<Result<Video>> getVideosByMovies(@Path("movieId") int movieId, @Query("api_key") String apiKey);
}
