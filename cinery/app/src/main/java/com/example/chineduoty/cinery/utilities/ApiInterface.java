package com.example.chineduoty.cinery.utilities;

import com.example.chineduoty.cinery.Constants;
import com.example.chineduoty.cinery.models.MovieResponse;
import com.example.chineduoty.cinery.models.ReviewResponse;
import com.example.chineduoty.cinery.models.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by chineduoty on 5/1/17.
 */

public interface ApiInterface {

    @GET("top_rated")
    Call<MovieResponse> getTopRatedMovies(@Query(Constants.API_KEY) String apiKey);

    @GET("popular")
    Call<MovieResponse> getPopularMovies(@Query(Constants.API_KEY) String apiKey);

    @GET("{id}")
    Call<MovieResponse> getMovieDetails(@Path("id") int id, @Query(Constants.API_KEY) String apiKey);

    @GET("{movie_id}/reviews")
    Call<ReviewResponse> getReviews(@Path("movie_id") int movieId, @Query(Constants.API_KEY) String apiKey);

    @GET("{movie_id}/videos")
    Call<TrailerResponse> getTrailers(@Path("movie_id") int movieId, @Query(Constants.API_KEY) String apiKey);
}
