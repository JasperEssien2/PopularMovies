package com.example.android.popularmovies.Rest;

import com.example.android.popularmovies.Constants.ApiConstant;
import com.example.android.popularmovies.Model.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    

    @GET(ApiConstant.MOVIE_MOST_POPULAR)
    Call<MovieResponse> getPopularMovies(@Query(ApiConstant.API_KEY_LABEL) String api_key,
                                         @Query(ApiConstant.PAGE_KEY) int pageNumber);

    @GET(ApiConstant.MOVIE_TOP_RATED)
    Call<MovieResponse> getTopRatedMovies(@Query(ApiConstant.API_KEY_LABEL) String api_key,
                                         @Query(ApiConstant.PAGE_KEY) int pageNumber);

    @GET(ApiConstant.SEARCH_MOVIE)
    Call<MovieResponse> getMovies(@Query(ApiConstant.API_KEY_LABEL) String api_key,
                                  @Query(ApiConstant.QUERY) String queryText,
                                  @Query(ApiConstant.PAGE_KEY) int pageNumber,
                                  @Query("") int queryType);

    @GET(ApiConstant.MOVIE_BY_ID)
    Call<MovieResponse> getMovieDetail(@Path(ApiConstant.ID_PLACE_HOLDER_KEY) int id,
                                       @Query(ApiConstant.API_KEY_LABEL) String apiKey,
                                       @Query(ApiConstant.APPEND_TO_RESPONSE) String credits);
}
