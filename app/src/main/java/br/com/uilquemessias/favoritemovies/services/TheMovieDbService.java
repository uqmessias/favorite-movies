package br.com.uilquemessias.favoritemovies.services;

import br.com.uilquemessias.favoritemovies.services.models.MovieResult;
import br.com.uilquemessias.favoritemovies.services.models.ReviewResult;
import br.com.uilquemessias.favoritemovies.services.models.VideoResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TheMovieDbService {
    @GET("movie/top_rated")
    Call<MovieResult> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieResult> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{movieId}/reviews")
    Call<ReviewResult> getReviewsByMovies(@Path("movieId") int movieId, @Query("api_key") String apiKey);

    @GET("movie/{movieId}/videos")
    Call<VideoResult> getVideosByMovies(@Path("movieId") int movieId, @Query("api_key") String apiKey);
}
