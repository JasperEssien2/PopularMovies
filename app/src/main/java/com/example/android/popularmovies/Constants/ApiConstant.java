package com.example.android.popularmovies.Constants;

import com.example.android.popularmovies.BuildConfig;

public class ApiConstant {

    public static final String API_KEY = BuildConfig.API_KEY;
    public static final String API_KEY_LABEL = "api_key";

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String POSTER_PATH_BASE_URL = "https://image.tmdb.org/t/p";
    public static final String POSTER_PATH_SIZE = "/w500";

    public static final String PAGE_KEY = "page";

    public static final String APPEND_TO_RESPONSE = "append_to_response";
    public static final String APPEND_ITEMS = "credits,videos,reviews";

    public static final String QUERY = "query";
    public static final String SEARCH_MOVIE = "search/movie";

    public static final String MOVIE_MOST_POPULAR = "movie/popular";
    public static final String MOVIE_TOP_RATED = "movie/top_rated";

    public static final String SORT_MOST_POPULAR = "popularity.desc";
    public static final String SORT_HIGHEST_RATED = "vote_average.desc";

    public static final String MOVIE_BY_ID = "movie/{id}";

    public static final String ID_PLACE_HOLDER_KEY = "id";

    public static final String YOUTUBE_APP_URI = "vnd.youtube:";
    public static final String YOUTUBE_BROWSER_URI = "http://www.youtube.com/watch?v=";

}
