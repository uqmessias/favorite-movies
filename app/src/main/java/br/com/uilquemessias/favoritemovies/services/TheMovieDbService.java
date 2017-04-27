package br.com.uilquemessias.favoritemovies.services;

import br.com.uilquemessias.favoritemovies.services.models.MovieResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMovieDbService {
    @GET("movie/top_rated")
    Call<MovieResult> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Call<MovieResult> getPopularMovies(@Query("api_key") String apiKey);
}
