package com.example.android.popularmovies.Activity;

import android.app.SearchManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Adapter.MovieAdapter;
import com.example.android.popularmovies.Constants.ApiConstant;
import com.example.android.popularmovies.Constants.BundleConstants;
import com.example.android.popularmovies.Fragments.MovieDetailBottomSheets;
import com.example.android.popularmovies.Interfaces.DatabaseCallbacks;
import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Model.MovieViewModel;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.SettingsSharedPreference;
import com.example.android.popularmovies.databinding.ActivityHomeBinding;
import com.example.android.popularmovies.utility.CustomScrollListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.GENRES;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.MOVIE_IMAGE_URL;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.MOVIE_RATING;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.MOVIE_RELEASE_DATE;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.MOVIE_STATUS;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.MOVIE_SYNOPSIS;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.MOVIE_TITLE;
import static com.example.android.popularmovies.Constants.ShareMovieDetailsConstant.TRAILER;

public class HomeActivity extends AppCompatActivity
        implements MovieAdapter.ListMovieActionModeViewCallbacks, DatabaseCallbacks {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int NUM_OF_COLUMNS_PORTRAIT_ORIENTATION = 3;
    private static final int NUM_OF_COLUMNS_HORIZONTAL_ORIENTATION = 5;
    private static final String EMPTY_STATE_TEXT = "No movies found!";
    private static final int PAGE_START = 1;
    private static final String CONNECTED = "connected!!";
    private static final String CONNECTING = "connecting...";
    private static final String NO_CONNECTION = "No Internet connection!";
    private static final int QUERY_TYPE_SEARCH = 490;
    private static final int QUERY_TYPE_DISCOVER = 410;
    public ActionMode mActionMode;
    private ActivityHomeBinding mActivityHomeBinding;
    private RecyclerView mMovieRecyclerView;
    private MovieAdapter mMovieAdapter;
    private BroadcastReceiver networkReceiver;
    private Snackbar mNetworkStatusSnackbar, mLoadingSnackBar;
    private GridLayoutManager mGridLayoutManager;
    private boolean isLoading = false;
    private int pageNumber = 1;
    private int totalPageNumber;
    private boolean isLastPage = false;
    private String mQueryText;
    private boolean isQueryTypeSearch = false;
    private SearchView searchView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MovieViewModel mMovieViewModel;
    private boolean isFavouriteButtonClicked = false;
    private int disconnectedCount = 0;
    private LiveData<List<Movie>> fromDatabase, fromApi;
    private Observer<List<Movie>> observerApi, observerDatabase;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHomeBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_home);
        SettingsSharedPreference.initSettingsSharedPrefernce(this);
        mMovieViewModel = ViewModelProviders
                .of(this)
                .get(MovieViewModel.class);
        MovieViewModel.setDatabaseCallbacks(this);
        setUpUi(savedInstanceState);
        if (!SettingsSharedPreference.getSortBySettings()
                .equals(SettingsSharedPreference.SORT_BY_SETTINGS_FAVOURITES))
            observeMovieLiveData();
        else onShowFavouriteFabClicked();
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //super.onReceive(context, intent);
                if (intent.getExtras() != null) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getBaseContext()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager == null) return;
                    NetworkInfo networkInfo =
                            connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        //Connected
                        if (disconnectedCount > 0) { //This if statement makes sure its only when a disconnection have occurred
                            onConnected();          //and its connected back that onConnected() is called to avoid calling Api multiple times
                            Log.e(TAG, " ++++++ IS_CONNECTED ++++++++++  ");
                        }
                    } else if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTING) {
                        //Connecting
                        onConnecting();
                    } else {
                        //No connection
                        disconnectedCount++;
                        onDisconnected();
                    }
                }
            }
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }

    private void showEmptyState() {
        mActivityHomeBinding.emptyStateLayout.getRoot().setVisibility(View.VISIBLE);
        mActivityHomeBinding.emptyStateLayout.emptyStateTextView.setTextColor(getColors(R.color.colorAccent));
        mActivityHomeBinding.emptyStateLayout.emptyStateTextView.setText(EMPTY_STATE_TEXT);
    }

    private void hideEmptyState() {
        mActivityHomeBinding.emptyStateLayout.getRoot().setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        updateOptionItemChangeTheme(menu);
        updateOptionItemSortBy(menu);
        implementSearchView(menu);
        return true;
    }

    private void implementSearchView(Menu menu) {
        SearchManager searchManager = (SearchManager)
                getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchManager == null) return;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Movie");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                mQueryText = query;
                isQueryTypeSearch = true;
                setObserverApi();
                loadFirstItems(null, QUERY_TYPE_SEARCH, query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.onActionViewExpanded();
        searchView.setQuery(mQueryText, false);
    }

    /**
     * This method checks which item in sortBy option menu item  selected and updates it accordingly
     *
     * @param menu menu to check
     */
    private void updateOptionItemSortBy(Menu menu) {
        this.menu = menu;
        int group = 1, popularity = 0, rating = 1, favourite = 2;

        if (SettingsSharedPreference.getSortBySettings().equals(
                SettingsSharedPreference.SORT_BY_SETTINGS_POPULARITY)) {

            menu.findItem(R.id.action_sort_movie).getSubMenu().getItem(popularity).setChecked(true);
            isFavouriteButtonClicked = false;
        }
        if (SettingsSharedPreference.getSortBySettings().equals(
                SettingsSharedPreference.SORT_BY_SETTINGS_RATING)) {
            menu.findItem(R.id.action_sort_movie).getSubMenu().getItem(rating).setChecked(true);
            isFavouriteButtonClicked = false;
        }

        if (SettingsSharedPreference.getSortBySettings().equals(
                SettingsSharedPreference.SORT_BY_SETTINGS_FAVOURITES)) {
            menu.findItem(R.id.action_sort_movie).getSubMenu().getItem(favourite).setChecked(true);
            isFavouriteButtonClicked = true;
        }
    }

    /**
     * This method checks which item in changeTheme option menu item selected and updates it accordingly
     *
     * @param menu menu to check
     */
    private void updateOptionItemChangeTheme(Menu menu) {
        int group = 2, dark = 1, light = 0;
        MenuItem menuItem = menu.findItem(R.id.action_change_theme);
        if (SettingsSharedPreference.getThemeSettings().equals(
                SettingsSharedPreference.THEME_SETTINGS_LIGHT)) {
            menuItem.getSubMenu().getItem(light).setChecked(true);

        }
        if (SettingsSharedPreference.getThemeSettings().equals(
                SettingsSharedPreference.THEME_SETTINGS_DARK)) {
            menuItem.getSubMenu().getItem(dark).setChecked(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_light_theme:
                item.setChecked(!item.isChecked());
                SettingsSharedPreference.setThemeSettings(SettingsSharedPreference.THEME_SETTINGS_LIGHT);
                setTheme();
                break;
            case R.id.action_dark_theme:
                item.setChecked(!item.isChecked());
                SettingsSharedPreference.setThemeSettings(SettingsSharedPreference.THEME_SETTINGS_DARK);
                setTheme();
                break;
            case R.id.action_sort_by_popularity:
                item.setChecked(!item.isChecked());
                SettingsSharedPreference.setSortBySettings(ApiConstant.SORT_MOST_POPULAR);
                isQueryTypeSearch = false;
                searchView.clearFocus();
                removeApiObserver();
                setObserverApi();
                isFavouriteButtonClicked = false;
                swipeRefreshLayout.setRefreshing(false);
                loadFirstItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_DISCOVER, null);
                break;
            case R.id.action_sort_by_rating:
                item.setChecked(!item.isChecked());
                isQueryTypeSearch = false;
                searchView.clearFocus();
                SettingsSharedPreference.setSortBySettings(ApiConstant.SORT_HIGHEST_RATED);
                removeApiObserver();
                setObserverApi();
                isFavouriteButtonClicked = false;
                swipeRefreshLayout.setRefreshing(false);
                loadFirstItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_DISCOVER, null);
                break;
            case R.id.action_sort_by_favourite:
                item.setChecked(!item.isChecked());
                isQueryTypeSearch = false;
                searchView.clearFocus();
                SettingsSharedPreference.setSortBySettings(SettingsSharedPreference.SORT_BY_SETTINGS_FAVOURITES);
                isFavouriteButtonClicked = true;
                onShowFavouriteFabClicked();
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //mOutState = outState;
        super.onSaveInstanceState(outState);
        putSelectedItemState(outState);
        putCurrentPageNumber(outState);
        putQueryType(outState);
        putQueryText(outState);
    }

    /**
     * This method puts all selected item position in the outState Bundle
     *
     * @param outState the outState
     */
    private void putSelectedItemState(Bundle outState) {
        if (mMovieAdapter.getSelectedItemPosition() != null) {
            outState.putIntegerArrayList(BundleConstants.SELECTED_ITEM_KRY,
                    mMovieAdapter.getSelectedItemPosition());
        }
    }

    /**
     * This method handles saving the users current page number
     * so that if orientation change occurs.. the user's current page will be reloaded
     *
     * @param outState type Bundle
     */
    private void putCurrentPageNumber(Bundle outState) {
        outState.putInt(BundleConstants.MOVIES_CURRENT_PAGE_NUM, pageNumber);
    }

    private void putQueryType(Bundle outState) {
        outState.putBoolean(BundleConstants.MOVIES_CURRENT_QUERY_TYPE, isQueryTypeSearch);
    }

    private void putQueryText(Bundle outState) {
        outState.putString(BundleConstants.MOVIES_CURRENT_QUERY_TEXT, mQueryText);
    }

    /**
     * Checks if Movie Detail Fragment is open and returns the fragment instance or null
     * so as to save instance
     *
     * @return the fragment instance or null
     */
    private MovieDetailBottomSheets movieFragment() {
        MovieDetailBottomSheets bottomSheets = (MovieDetailBottomSheets)
                getSupportFragmentManager()
                        .findFragmentByTag(BundleConstants.MOVIE_DETAIL_TAG);

        if (bottomSheets != null) return bottomSheets;
        else return null;
    }

    @Override
    public void itemDeletedSuccessfully() {
        Toast.makeText(this, "Item Deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemInsertedSuccessfully() {
        Toast.makeText(this, "Added to Favourites", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemAlreadyExistInDatabase(boolean addItem, boolean isBottomSheet, boolean doItemExist) {
        MovieDetailBottomSheets bottomSheets = movieFragment();
        if (addItem) ;
            //Toast.makeText(this, "Already added", Toast.LENGTH_SHORT).show();
            //mMovieViewModel.deleteItem();
        else {
            if (isBottomSheet) {
                if (bottomSheets != null) {
                    bottomSheets.updateFab(doItemExist);
                }
            }
        }
    }

    @Override
    public void allItemSuccessfulyInserted() {

    }

    @Override
    public void dismissLoadingSnackbar() {
        if (mLoadingSnackBar != null)
            mLoadingSnackBar.dismiss();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            restoreItemSelectedState(savedInstanceState);
            restorePageNumber(savedInstanceState);
//            restoreQueryType(savedInstanceState);
//            restoreQueryText(savedInstanceState);
        }
        //TODO: Check this line make sure its correct
        //This line is added so that.. when configuration changes occurs it scrolls to the last viewed item
//        mMovieRecyclerView.scrollToPosition(mGridLayoutManager.findLastCompletelyVisibleItemPosition());
    }

    /**
     * Check if items were selected before onSaveInstanceState() was called
     * if some items were selected it re-selects them
     *
     * @param savedInstanceState restoreStateBundle
     */
    private void restoreItemSelectedState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(BundleConstants.SELECTED_ITEM_KRY)) {
            ArrayList<Integer> selectedPos =
                    savedInstanceState.getIntegerArrayList(BundleConstants.SELECTED_ITEM_KRY);
            if (selectedPos == null) return;
            if (selectedPos.isEmpty()) return;
            for (Integer pos : selectedPos) {
                mMovieAdapter.toggleSelection(pos);
            }
        }
    }

    private void restorePageNumber(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(BundleConstants.MOVIES_CURRENT_PAGE_NUM)) {
            pageNumber = savedInstanceState.getInt(BundleConstants.MOVIES_CURRENT_PAGE_NUM, 1);
        }
    }

    /**
     * If configuration change occurs where onCreate will have to be called.. This method gets the last query type
     * before the change occured
     *
     * @param savedInstanceState
     */
    private void restoreQueryType(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(BundleConstants.MOVIES_CURRENT_QUERY_TYPE))
            isQueryTypeSearch = savedInstanceState.getBoolean(BundleConstants.
                    MOVIES_CURRENT_QUERY_TYPE, false);
    }

    private void restoreQueryText(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(BundleConstants.MOVIES_CURRENT_QUERY_TEXT)) {
            mQueryText = savedInstanceState.getString(BundleConstants
                    .MOVIES_CURRENT_QUERY_TEXT);
        }
    }

    /**
     * This method is responsible for observing changes made to the list, updating the neccessay Ui
     * according to the change in data
     */
    private void observeMovieLiveData() {
        mActivityHomeBinding.moviesLoadingProgressbar.setVisibility(View.VISIBLE);
        int queryType = 0;
        if (isQueryTypeSearch)
            queryType = QUERY_TYPE_SEARCH;
        else
            queryType = QUERY_TYPE_DISCOVER;
        fromApi =
                mMovieViewModel
                        .getMovieListLiveData(SettingsSharedPreference.getSortBySettings(), queryType, mQueryText);
        observerApi = new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                mActivityHomeBinding.moviesLoadingProgressbar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                totalPageNumber = mMovieViewModel.getTotalPageNumber();
                if (!mMovieViewModel.isQuerySuccessful()) pageNumber--;
                if (movies == null) {
                    showEmptyState();
                    return;
                }

                if (movies.isEmpty()) {
                    showEmptyState();
                    return; //TODO: Show empty state view
                }
                if (pageNumber == PAGE_START) {
                    mMovieAdapter.clearList();
                    mMovieAdapter.addMovieList(movies);
                } else {
                    isLoading = false;
                    mLoadingSnackBar.dismiss();
                    Log.e(TAG, "FormerListSize: -- " + mMovieViewModel.getFormerListSize() + " ------- ");
                    if (mMovieViewModel.getFormerListSize() < movies.size()) //TODO: Test the usefulness of this line of code
                        mMovieAdapter.addMovieList(movies.subList(mMovieViewModel.getFormerListSize(),
                                movies.size()));
                }
                if (pageNumber <= totalPageNumber) mMovieAdapter.updateIsLoadingTrue();
                else isLastPage = true;
            }
        };
        fromApi.observe(this, observerApi);
    }

    /**
     * Sets the observer for movie items livedata gotten from Api, while it removes the observer
     * of movie list gotten from the database
     */
    private void setObserverApi() {
        if (observerDatabase != null)
            fromDatabase.removeObserver(observerDatabase);
        observeMovieLiveData();
    }

    /**
     * This method is responsible for setting up the UI of HomeActivty
     *
     * @param savedInstanceState
     */
    private void setUpUi(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            restoreQueryType(savedInstanceState);
            restoreQueryText(savedInstanceState);
        }
        setTheme();
        setUpRecyclerView();
        setUpOnSwipeAction();
        mNetworkStatusSnackbar = Snackbar.make(mActivityHomeBinding.getRoot(), "", Snackbar.LENGTH_INDEFINITE);
        mActivityHomeBinding.starFab.bringToFront();
        mActivityHomeBinding.starFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShowFavouriteFabClicked();
                isFavouriteButtonClicked = true;
                SettingsSharedPreference.setSortBySettings(SettingsSharedPreference.SORT_BY_SETTINGS_FAVOURITES);
                menu.findItem(R.id.action_sort_movie).getSubMenu().getItem(2).setChecked(true);
            }
        });
        setUpSnackBarTheme(mNetworkStatusSnackbar);
        mLoadingSnackBar =
                Snackbar.make(mActivityHomeBinding.activityHome, "Loading more.....", Snackbar.LENGTH_INDEFINITE);
        setUpSnackBarTheme(mLoadingSnackBar);
    }

    /**
     * This methods define the action that takes place when show favourites fab clicked
     */
    private void onShowFavouriteFabClicked() {
        observerDatabase = new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> movies) {
                if (movies == null) return;
                if (movies.isEmpty()) {
                    showEmptyState();
                    return;
                }
                mMovieAdapter.clearList();
                mMovieAdapter.addMovieList(movies);
            }
        };
        mMovieAdapter.clearList();
        fromDatabase =
                mMovieViewModel.getMoviesFromDatabase();
        removeApiObserver();

        fromDatabase
                .observe(this, observerDatabase);
    }

    /**
     * This method handles the removal of Livedata<Movie> observer gotten from the Api
     */
    private void removeApiObserver() {
        if (fromApi != null)
            fromApi.removeObserver(observerApi);
    }

    /**
     * This method customises any snack bar passed to it
     *
     * @param snackbar the snack bar to customise
     */
    private void setUpSnackBarTheme(Snackbar snackbar) {
        snackbar.getView().setBackgroundColor(getColors(R.color.colorPrimary));
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(getColors(R.color.colorAccent));
    }

    /**
     * This method is responsible for preparing the recyclerView
     */
    private void setUpRecyclerView() {
        mGridLayoutManager = checkConfiguration();
        mMovieRecyclerView = mActivityHomeBinding.movieRecycler;
        mMovieAdapter = new MovieAdapter(new ArrayList<Movie>(), this, mActivityHomeBinding);
        mMovieAdapter.setActionListSelected(this);
        mMovieRecyclerView.setLayoutManager(mGridLayoutManager);
        mMovieRecyclerView.addItemDecoration(new GridLayoutItemDecorator(4));
        mMovieRecyclerView.setAdapter(mMovieAdapter);
        mMovieRecyclerView.addOnScrollListener(new CustomScrollListener(mGridLayoutManager) {
            @Override
            public void loadMoreItems() {
                //If current list in adapter is less than 20.. no need showing loading snack bar
//                if (!isFavouriteButtonClicked) {
                isLoading = mMovieAdapter.getItemCount() >= 20;
                pageNumber++;
                mLoadingSnackBar.show();
                if (mMovieAdapter.getItemCount() <= 20) mLoadingSnackBar.dismiss();
                if (isQueryTypeSearch)
                    loadNextItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_SEARCH, mQueryText);
                else
                    loadNextItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_DISCOVER, mQueryText);
                //              }
            }

            @Override
            public int getTotalPageCount() {
                return totalPageNumber;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    /**
     * This method setup what happens when a refresh swipe is done
     */
    private void setUpOnSwipeAction() {
        swipeRefreshLayout =
                mActivityHomeBinding.swipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isFavouriteButtonClicked) {
                    removeApiObserver();
                    setObserverApi();
                    if (isQueryTypeSearch)
                        loadFirstItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_SEARCH,
                                mQueryText);
                    else
                        loadFirstItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_DISCOVER,
                                mQueryText);
                } else swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * This method takes care of getting the list of movies from the API
     */
    private void loadFirstItems(String sortBy, int queryType, String query) {
        //isFavouriteButtonClicked = false;
        mMovieAdapter.updateIsLoadingFalse();
        hideEmptyState();
        mActivityHomeBinding.moviesLoadingProgressbar.setVisibility(View.VISIBLE);
        mMovieViewModel.getFirstItemMoviesFromApi(sortBy, queryType, query);
        pageNumber = PAGE_START;
        if (queryType == QUERY_TYPE_SEARCH) {
        } else {
            isQueryTypeSearch = false;
        }
    }

    /**
     * This method takes care of loading the next items in the list
     *
     * @param sortBy    parameter to know how to sort the list
     * @param queryType query type
     * @param query     query string
     */
    private void loadNextItems(String sortBy, int queryType, String query) {
        mMovieAdapter.updateIsLoadingFalse();
        hideEmptyState();
        mMovieViewModel.getNextMovieItemsFromApi(sortBy, queryType, query, pageNumber);

        if (queryType == QUERY_TYPE_SEARCH) {
        } else {
            if (sortBy.equals(SettingsSharedPreference.SORT_BY_SETTINGS_POPULARITY)) {
            } else if (sortBy.equals(SettingsSharedPreference.SORT_BY_SETTINGS_RATING)) {
            }
        }
    }

    /**
     * If network connected indicate that its connected using a snack bar
     */
    private void onConnected() {
        mActivityHomeBinding.moviesLoadingProgressbar.setVisibility(View.GONE);
        mNetworkStatusSnackbar.setText(CONNECTED);
        mNetworkStatusSnackbar.dismiss();
        if (isQueryTypeSearch)
            loadFirstItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_SEARCH, mQueryText);
        else
            loadFirstItems(SettingsSharedPreference.getSortBySettings(), QUERY_TYPE_DISCOVER, mQueryText);
    }

    /**
     * If network connecting indicate that its connecting using a snack bar
     */
    private void onConnecting() {
        mActivityHomeBinding.moviesLoadingProgressbar.setVisibility(View.VISIBLE);
        mNetworkStatusSnackbar.setText(CONNECTING);
        mNetworkStatusSnackbar.show();
    }

    /**
     * If network is disconnected indicate that its disconnected using a snack bar
     */
    private void onDisconnected() {
        mActivityHomeBinding.moviesLoadingProgressbar.setVisibility(View.GONE);
        mNetworkStatusSnackbar.setText(NO_CONNECTION);
        mNetworkStatusSnackbar.show();
    }

    /**
     * This method checks the orientation of the device if device is portrait it sets
     * mGridLayoutManager to display 3 columns
     * if landscape it set to display 5 columns
     * if non it displays 2 columns
     *
     * @return it returns GridLayoutManager
     */
    private GridLayoutManager checkConfiguration() {
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT) {
            return new GridLayoutManager(this,
                    NUM_OF_COLUMNS_PORTRAIT_ORIENTATION, GridLayoutManager.VERTICAL, false);
        }
        if (this.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            return new GridLayoutManager(this,
                    NUM_OF_COLUMNS_HORIZONTAL_ORIENTATION, GridLayoutManager.VERTICAL, false);
        }
        return new GridLayoutManager(this, 2);
    }

    @Override
    public void onListItemSelect(int position) {
        mMovieAdapter.toggleSelection(position);

        final boolean hasCheckedItems = mMovieAdapter.getSelectedItems().size() > 0;

        if (hasCheckedItems && mActionMode == null) {
            mActionMode = startSupportActionMode(new ListMovieToolbarActionCallbacks(
                    this, mMovieAdapter));
        } else if (!hasCheckedItems && mActionMode != null) mActionMode.finish();

        if (mActionMode != null) {
            mActionMode.setTitle(String.valueOf(mMovieAdapter.getSelectedCount()));
        }
    }

    @Override
    public void onDestroyActionMode() {
        mActionMode = null;
        mMovieAdapter.clearSelections();
    }

    @Override
    public void onShareActionButton() {
        shareText(getSelectedItemString());
        mMovieAdapter.clearSelections();
    }

    @Override
    public void onAddToStarredList() {
        //TODO: WILL BE IMPLEMENTED NEXT STAGE OF THE APP
        addToStarredItems();
        mMovieAdapter.clearSelections();
    }

    @Override
    public void onDeleteActionButton() {
        deleteStarredItems();
        mMovieAdapter.clearSelections();
    }

    @Override
    public void onCancelActionButton() {

    }

    /**
     * This method handles the task of sharing the text when the share menu option is cicked
     *
     * @param textToShare the text to share
     */
    private void shareText(String textToShare) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * This method get's the selected item details
     *
     * @return the String to share
     */
    private String getSelectedItemString() {
        StringBuilder stringToShare = new StringBuilder();
        for (Movie movie : mMovieAdapter.getSelectedItems()) {
            stringToShare.append(prettyPrintString(movie));
        }

        return stringToShare.toString();
    }

    /**
     * This method is responsible for adding movie item to the database
     */
    private void addToStarredItems() {
        for (Movie movie : mMovieAdapter.getSelectedItems())
            mMovieViewModel.isMovieInDatabase(movie, true, false);
    }

    /**
     * This method is responsible for deleting selected movies from database
     */
    private void deleteStarredItems() {
        Log.e(TAG, "SelectedItems: _______ " + mMovieAdapter.getSelectedItems() + " ___________");
        for (Movie movie : mMovieAdapter.getSelectedItems()) {
            mMovieViewModel.deleteItem(movie);
            Log.e(TAG, "Deleted --- " + movie.getOriginalTitle() + " -------");
        }
    }

    /**
     * This method helps to format the movie details
     *
     * @param movie the selected movie
     * @returns formatted String
     */
    private String prettyPrintString(Movie movie) {
        return "**" + MOVIE_TITLE + movie.getOriginalTitle() + "**\n" +
                MOVIE_IMAGE_URL + ApiConstant.POSTER_PATH_BASE_URL +
                ApiConstant.POSTER_PATH_SIZE + movie.getMovieImageUrl() +
                MOVIE_SYNOPSIS + movie.getPlotSynopsis() + "\n" +
                MOVIE_RATING + movie.getRating() + "\n" +
                MOVIE_RELEASE_DATE + movie.getReleaseDate() + "\n" +
                MOVIE_STATUS + movie.getMovieStatus() + "\n" +
                GENRES + getGenres(movie.getGenres()) + "\n" +
                TRAILER + ApiConstant.YOUTUBE_BROWSER_URI + movie.getTrailers()
                .get(0).getTrailerId() + "\n\n";
    }

    private String getGenres(List<Movie.MovieGenre> genres) {
        StringBuilder stringBuilder = new StringBuilder();
        int count = 0;
        for (Movie.MovieGenre movieGenre : genres) {
            if (count != genres.size() - 1)
                stringBuilder.append(movieGenre.getGenre() + ", ");
            else if (count == genres.size() - 1)
                stringBuilder.append(movieGenre.getGenre() + ".");

            count++;
        }
        return stringBuilder.toString();
    }

    /**
     * This method checks the SharedPreference for the user's choice theme
     * and updates the theme accordinly
     */
    private void setTheme() {
        switch (SettingsSharedPreference.getThemeSettings()) {
            case SettingsSharedPreference.THEME_SETTINGS_LIGHT:
                lightTheme();
                break;
            case SettingsSharedPreference.THEME_SETTINGS_DARK:
                darkTheme();
                break;
        }
    }

    /**
     * Used for setting a lightTheme according to the user's choice
     */
    private void lightTheme() {
        mActivityHomeBinding.activityHome.setBackgroundResource(R.color.colorAppBackgroundLight);
    }

    /**
     * Used for setting a darkTheme according to the user's choice
     */
    private void darkTheme() {
        mActivityHomeBinding.activityHome.setBackgroundResource(R.color.colorAppBackgroundDark);
    }

    /**
     * This method is responsible for returning an int color value
     *
     * @param resource the color resource given
     * @return the color int value
     */
    private int getColors(@ColorRes int resource) {
        return ContextCompat.getColor(this, resource);
    }


    /**
     * This class implements an ActionCallback preparing the action bar for item onLongClick
     */
    private class ListMovieToolbarActionCallbacks implements ActionMode.Callback {

        private final MovieAdapter.ListMovieActionModeViewCallbacks actionModeViewCallbacks;
        private final MovieAdapter movieAdapter;

        ListMovieToolbarActionCallbacks(
                final MovieAdapter.ListMovieActionModeViewCallbacks actionModeViewCallbacks,
                MovieAdapter movieAdapter
        ) {
            this.actionModeViewCallbacks = actionModeViewCallbacks;
            this.movieAdapter = movieAdapter;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.movie_item_selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            menu.findItem(R.id.action_star).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.findItem(R.id.action_share).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_share:
                    actionModeViewCallbacks.onShareActionButton();
                    mode.finish();
                    return true;
                case R.id.action_star:
                    actionModeViewCallbacks.onAddToStarredList();
                    mode.finish();
                    return true;
                case R.id.action_delete:
                    Log.e(TAG, "IsFavouriteFabCLicked:   " + isFavouriteButtonClicked + " }}");
                    if (isFavouriteButtonClicked) { //This if statement is meant to ensure that only
                        actionModeViewCallbacks    // when favourite items are shown then the user can delete
                                .onDeleteActionButton();
                        mode.finish();
                        return true;
                    }
                case R.id.action_close:
                    actionModeViewCallbacks.onCancelActionButton();
                    mode.finish();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionModeViewCallbacks.onDestroyActionMode();
            movieAdapter.clearSelections();
        }
    }

    private class GridLayoutItemDecorator extends RecyclerView.ItemDecoration {
        int margin;

        public GridLayoutItemDecorator(int margin) {
            this.margin = margin;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = margin;
            outRect.right = margin;
            outRect.bottom = margin;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildLayoutPosition(view) == 0) {
                outRect.top = margin;
            } else {
                outRect.top = 0;
            }
        }
    }
}
