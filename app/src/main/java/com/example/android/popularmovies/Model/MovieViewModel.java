package com.example.android.popularmovies.Model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.Constants.ApiConstant;
import com.example.android.popularmovies.Database.MovieFavouriteDatabase;
import com.example.android.popularmovies.Interfaces.DatabaseCallbacks;
import com.example.android.popularmovies.Rest.ApiClient;
import com.example.android.popularmovies.Rest.ApiInterface;
import com.example.android.popularmovies.SettingsSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieViewModel extends AndroidViewModel {
    private static final int QUERY_TYPE_SEARCH = 490;
    private static final int QUERY_TYPE_DISCOVER = 410;
    private static final int PAGE_START = 1;
    private static final String TAG = MovieViewModel.class.getSimpleName();
    private static DatabaseCallbacks databaseCallbacks;
    private static DatabaseCallbacks databaseBottomSheetCallbacks;
    private MovieFavouriteDatabase movieFavouriteDatabase;
    private List<Movie> movieList = new ArrayList<>();
    private LiveData<List<Movie>> movieListLiveData;
    private MutableLiveData<List<Movie>> mutableLiveData;// = new MutableLiveData<>();
    private int pageNumber = 1;
    private int totalPageNumber;
    private int formerListSize;
    private boolean isLastPage = false;
    private String mQueryText;
    private boolean isQueryTypeSearch = false;
    private boolean isQuerySuccessful = false;


    public MovieViewModel(@NonNull Application application) {
        super(application);
        movieFavouriteDatabase = MovieFavouriteDatabase.getDatabase(this.getApplication());
    }

    public static void setDatabaseCallbacks(DatabaseCallbacks databaseCallbacks) {
        MovieViewModel.databaseCallbacks = databaseCallbacks;
    }

    public static void setDatabaseBottomSheetCallbacks(DatabaseCallbacks databaseCallbacks) {
        MovieViewModel.databaseBottomSheetCallbacks = databaseCallbacks;
    }

    public LiveData<List<Movie>> getMoviesFromDatabase() {
        movieList.clear();
        //mutableLiveData = (MutableLiveData<List<Movie>>) movieFavouriteDatabase.movieDao().getAllMovies());
        movieListLiveData = movieFavouriteDatabase.movieDao().getAllMovies();
        //Log.e(TAG, "FAV --- " + movieFavouriteDatabase.movieDao().getAllMovies().getValue().toString());
        //mutableLiveData.setValue(movieFavouriteDatabase.movieDao().getAllMovies().getValue());
        return movieListLiveData;
    }

    private void setMoviesListLiveData() {
        mutableLiveData.setValue(movieList);

    }

    public void getFirstItemMoviesFromApi(String sortBy, int queryType, String query) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = null;
        formerListSize = movieList.size();
        pageNumber = PAGE_START;
        if (queryType == QUERY_TYPE_SEARCH) {
            call = apiService.getMovies(
                    ApiConstant.API_KEY,
                    query, PAGE_START, QUERY_TYPE_SEARCH);
        } else {
            isQueryTypeSearch = false;
            if (sortBy.equals(SettingsSharedPreference.SORT_BY_SETTINGS_POPULARITY)) {
                call = apiService.getPopularMovies(ApiConstant.API_KEY,
                        PAGE_START);
            } else if (sortBy.equals(SettingsSharedPreference.SORT_BY_SETTINGS_RATING)) {
                call = apiService.getTopRatedMovies(ApiConstant.API_KEY,
                        PAGE_START);
            }
        }
        if (call == null) return;
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MovieResponse movieResponse = response.body();
                isQuerySuccessful = true;
                if (movieResponse == null) return;
                totalPageNumber = movieResponse.getTotalPages();

                List<Movie> movies = movieResponse.getResults();
                //movieList.clear(); //TODO: Check if this line of code solves cases of results of first load being duplicated
                movieList = movies;
                setMoviesListLiveData();
                if (movies != null)
                    if (movies.isEmpty()) {
                        //showEmptyState();
                        return; //TODO: Show empty state view
                    }
                if (pageNumber <= totalPageNumber) ;//mMovieAdapter.updateIsLoadingTrue();
                else isLastPage = true;
                if (movies == null) ;//showEmptyState();//TODO: Show empty state view
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isQuerySuccessful = false;
                t.printStackTrace();
            }
        });
    }

    public void getNextMovieItemsFromApi(String sortBy, int queryType, String query, int pageNumber) {
        this.pageNumber = pageNumber;
        Log.e(TAG, "********************** PageNumber: (" + pageNumber + ") ********* ");
        formerListSize = movieList.size();
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = null;
        if (queryType == QUERY_TYPE_SEARCH) {
            call = apiService.getMovies(
                    ApiConstant.API_KEY,
                    query, this.pageNumber, QUERY_TYPE_SEARCH);
        } else {
            if (sortBy.equals(SettingsSharedPreference.SORT_BY_SETTINGS_POPULARITY)) {
                call = apiService.getPopularMovies(ApiConstant.API_KEY,
                        this.pageNumber);
            } else if (sortBy.equals(SettingsSharedPreference.SORT_BY_SETTINGS_RATING)) {
                call = apiService.getTopRatedMovies(ApiConstant.API_KEY,
                        this.pageNumber);
            }
        }
        if (call == null) return;
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isQuerySuccessful = true;
                MovieResponse movieResponse = response.body();
                if (movieResponse == null) return;
                totalPageNumber = movieResponse.getTotalPages();

                List<Movie> movies = movieResponse.getResults();
                movieList.addAll(movies);
                setMoviesListLiveData();
                if (movies != null)
                    if (movies.isEmpty()) {
                        //showEmptyState();
                        return; //TODO: Show empty state view
                    }
                if (MovieViewModel.this.pageNumber != totalPageNumber)
                    isLastPage = false;//mMovieAdapter.updateIsLoadingTrue();
                else isLastPage = true;
                if (movies == null) ;//TODO: Show empty state view
                databaseCallbacks.dismissLoadingSnackbar();
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isQuerySuccessful = false;
                t.printStackTrace();
                databaseCallbacks.dismissLoadingSnackbar();
            }
        });
    }

    public int getTotalPageNumber() {
        return totalPageNumber;
    }

    public int getFormerListSize() {
        return formerListSize;
    }


    /**
     * This method helps query the first items of the API
     *
     * @param sortBy    a string to indicate what type of movie list to get from API
     * @param queryType int to indicate queryType if its a search query or discover query
     * @param query     a string for search query
     * @return LiveData<List                                                                                                                               <                                                                                                                               Movie>> object
     */
    public LiveData<List<Movie>> getMovieListLiveData(String sortBy, int queryType, String query) {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<>();
            getFirstItemMoviesFromApi(sortBy, queryType, query);
        }
        return mutableLiveData;
    }

    public void deleteItem(Movie movie) {
        new DeleteMovieAsyncTask(movieFavouriteDatabase).execute(movie);
    }

    public void addItem(Movie movie) {
        new AddMovieAsyncTask(movieFavouriteDatabase).execute(movie);
    }

    public boolean isQuerySuccessful() {
        return isQuerySuccessful;
    }

    public boolean isMovieInDatabase(Movie movie, boolean addItem, boolean isBottomSheet) {
        GetMovieByIdAsyncTask getMovieByIdAsyncTask =
                new GetMovieByIdAsyncTask(movieFavouriteDatabase, addItem, this,
                        isBottomSheet, movie);
        //getMovieByIdAsyncTask.
        getMovieByIdAsyncTask.execute(movie.getId());
        return false;
    }

    private static class DeleteMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieFavouriteDatabase movieFavouriteDatabase;

        public DeleteMovieAsyncTask(MovieFavouriteDatabase movieFavouriteDatabase) {
            super();
            this.movieFavouriteDatabase = movieFavouriteDatabase;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieFavouriteDatabase.movieDao().deleteMovie(movies[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            databaseCallbacks.itemDeletedSuccessfully();
            databaseBottomSheetCallbacks.itemDeletedSuccessfully();
        }
    }

    private static class AddMovieAsyncTask extends AsyncTask<Movie, Void, Void> {
        private MovieFavouriteDatabase movieFavouriteDatabase;

        public AddMovieAsyncTask(MovieFavouriteDatabase movieFavouriteDatabase) {
            super();
            this.movieFavouriteDatabase = movieFavouriteDatabase;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            movieFavouriteDatabase.movieDao().addMovie(movies[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            databaseCallbacks.itemInsertedSuccessfully();
            databaseBottomSheetCallbacks.itemInsertedSuccessfully();
        }
    }

    private static class GetMovieByIdAsyncTask extends AsyncTask<Integer, Void, Movie> {
        private MovieFavouriteDatabase movieFavouriteDatabase;
        private boolean addItem;
        private MovieViewModel viewModel;
        private boolean isBottomSheet;
        private Movie movieItem;

        public GetMovieByIdAsyncTask(MovieFavouriteDatabase movieFavouriteDatabase, boolean addItem,
                                     MovieViewModel viewModel, boolean isBottomSheet,
                                     Movie movieItem) {
            super();
            this.movieFavouriteDatabase = movieFavouriteDatabase;
            this.addItem = addItem;
            this.viewModel = viewModel;
            this.isBottomSheet = isBottomSheet;
            this.movieItem = movieItem;
        }

        @Override
        protected void onPostExecute(Movie movie) {
            if (addItem) {
                if (movie == null) {
                    viewModel.addItem(movieItem);
                } else {
                    databaseCallbacks.itemAlreadyExistInDatabase(addItem, isBottomSheet, true);
                    viewModel.deleteItem(movieItem);
                }
            } else {
                if (movie == null) databaseCallbacks.itemAlreadyExistInDatabase(addItem,
                        isBottomSheet, false);
                else
                    databaseCallbacks.itemAlreadyExistInDatabase(addItem, isBottomSheet, true);
            }
        }

        @Override
        protected Movie doInBackground(Integer... integers) {
            return movieFavouriteDatabase.movieDao().getMovieById(integers[0]);
        }
    }
}
