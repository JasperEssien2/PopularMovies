package com.example.android.popularmovies.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.android.popularmovies.Adapter.MovieAdapter;
import com.example.android.popularmovies.Constants.ApiConstant;
import com.example.android.popularmovies.Constants.BundleConstants;
import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Model.MovieResponse;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Rest.ApiClient;
import com.example.android.popularmovies.Rest.ApiInterface;
import com.example.android.popularmovies.SettingsSharedPreference;
import com.example.android.popularmovies.databinding.MovieDetailLayoutBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MovieDetailBottomSheets extends BottomSheetDialogFragment {
    private static final String TAG = MovieDetailBottomSheets.class.getSimpleName();
    private MovieDetailLayoutBinding mMovieDetailLayoutBinding;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };
    private Movie movie;
    private LinearLayoutManager layoutManager;
    private MovieAdapter.MovieCastCrewAdapter adapter;

    //TODO: create a bottom horizontal recyclerview where all the cast image will be shown when any is clicked
    //TODO: it will show the details, the role they acted etc!
    public MovieDetailBottomSheets() {
        super();
    }

    public static MovieDetailBottomSheets newInstance(Bundle bundle) {
        MovieDetailBottomSheets fragment = new MovieDetailBottomSheets();
        fragment.setStyle(BottomSheetDialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Translucent);
        fragment.setStyle(BottomSheetDialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Translucent);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SettingsSharedPreference.initSettingsSharedPrefernce(getContext());
        layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        mMovieDetailLayoutBinding = DataBindingUtil
                .inflate(LayoutInflater.from(getContext()), R.layout.movie_detail_layout,
                        (ViewGroup) dialog.getCurrentFocus(), false);
        mMovieDetailLayoutBinding.movieDetailCastRecyclerView.setLayoutManager(layoutManager);
        if (getActivity() == null) return;
        adapter = new MovieAdapter.MovieCastCrewAdapter(new ArrayList<Movie.MovieCastCrew>(), getContext(),
                getActivity().getSupportFragmentManager(),
                BundleConstants.MOVIE_CAST_DETAIL_TYPE);
        mMovieDetailLayoutBinding.movieDetailStar.bringToFront();
        setUpTheme();
        if (getArguments() != null)
            bindDataWithViews(getArguments());
        dialog.setContentView(mMovieDetailLayoutBinding.getRoot());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View)
                mMovieDetailLayoutBinding.getRoot().getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetCallback);
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //If device is in landscape mode.. get the height of device, dive the height by 2 and
                // set the peek height.
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                ((BottomSheetBehavior) behavior).setPeekHeight(height / 2);
                Log.e(TAG, "Configuration Changes: Peek Height: " + ((BottomSheetBehavior) behavior).getPeekHeight());
            }

            Log.e(TAG, "behavior not null: Peek Height: " + ((BottomSheetBehavior) behavior).getPeekHeight());
        } else {
            Log.e(TAG, "behaviour IS NULL");
        }
    }

    private void callMovieCast(int id, List<Movie.MovieCastCrew> castCrewList) {
        if (castCrewList == null) {
            ApiInterface apiService =
                    ApiClient.getClient().create(ApiInterface.class);
            Call<MovieResponse> call = apiService
                    .getMovieDetail(id, ApiConstant.API_KEY, ApiConstant.CREDITS);
            call.enqueue(new retrofit2.Callback<MovieResponse>() {
                @Override
                public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                    Log.e(TAG, "::::::::::::::::::" + response.message() + "::::::::::::::::::");
                    MovieResponse movieResponse = response.body();
                    if (movieResponse == null) return;
                    Movie.MovieCredits credit = movieResponse.getCredit();
                    Log.e(TAG, "::::::::::::::::::" + credit.toString() + "::::::::::::::::::");
                    adapter.setList(credit.getCasts());
                }

                @Override
                public void onFailure(Call<MovieResponse> call, Throwable t) {
                    Log.e(TAG, "URL: -- ::::::::::::::::::" + call.request().url().toString() + "::::::::::::::::::");
                    Log.e(TAG, "::::::::::::::::::" + t.getMessage() + "::::::::::::::::::");
                }
            });
        }
    }

    private void setUpTheme() {
        if (SettingsSharedPreference.getThemeSettings().equals(
                SettingsSharedPreference.THEME_SETTINGS_DARK)) {
            darkTheme();
        } else if (SettingsSharedPreference.getThemeSettings().equals(
                SettingsSharedPreference.THEME_SETTINGS_LIGHT)) {
            lightTheme();
        }
    }

    private void bindDataWithViews(Bundle bundle) {
        movie = bundle.getParcelable(BundleConstants.MOVIE_DETAIL_TAG);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        if (movie != null) {
            callMovieCast(movie.getId(), movie.getCasts());
            mMovieDetailLayoutBinding.movieDetailCastRecyclerView.setLayoutManager(layoutManager);
            mMovieDetailLayoutBinding.movieDetailCastRecyclerView
                    .setAdapter(new MovieAdapter.MovieCastCrewAdapter(movie.getCasts(), getContext(),
                            getActivity().getSupportFragmentManager(),
                            BundleConstants.MOVIE_CAST_DETAIL_TYPE));
            loadMovieImage(movie.getMovieImageUrl());
            mMovieDetailLayoutBinding.movieDetailTitle.setText(checkIfStringEmpty(movie.getOriginalTitle()));
            mMovieDetailLayoutBinding.movieDetailImageView.setContentDescription(movie.getOriginalTitle());
            mMovieDetailLayoutBinding.movieDetailRatingTv.setText(
                    String.valueOf(movie.getRating() + "/10.0"));
            mMovieDetailLayoutBinding.movieDetailStatusTv.setText(checkIfStringEmpty(movie.getMovieStatus()));
            mMovieDetailLayoutBinding.movieDetailSynopsisTv.setText(checkIfStringEmpty(movie.getPlotSynopsis()));
            mMovieDetailLayoutBinding.movieDetailReleaseDateTv.setText(checkIfStringEmpty(movie.getReleaseDate()));
        }
    }

    public Movie getMovieInstance() {
        return movie;
    }

    /**
     * Checks if the given string is null or empty
     *
     * @param string the string to check.
     * @return if string not empty or null it returns the given string else it returns a string
     * indicating its empty or null
     */
    private String checkIfStringEmpty(String string) {
        if (string == null) return " ------- ";
        if (string.isEmpty()) return " ------- ";
        return string;
    }

    /**
     * This helper method loads the imageUrl to the imageView
     *
     * @param imageUrl the image url
     */
    private void loadMovieImage(final String imageUrl) {
        //setProgressBarVisible(mMovieDetailLayoutBinding.movieDetailImageProgressBar);
        Log.e(TAG, "****** ------ " + ApiConstant.POSTER_PATH_BASE_URL +
                ApiConstant.POSTER_PATH_SIZE + imageUrl + " ------ ********");
        Picasso
                .get()
                .load(ApiConstant.POSTER_PATH_BASE_URL +
                        ApiConstant.POSTER_PATH_SIZE + imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.image_url_broken)
                .into(mMovieDetailLayoutBinding.movieDetailImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mMovieDetailLayoutBinding.movieDetailImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        setProgressBarGone(
                                mMovieDetailLayoutBinding.movieDetailImageProgressBar);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Picsso Loading Errror ------------- " + e.getMessage() + "********* ");
                        mMovieDetailLayoutBinding.movieDetailImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        setProgressBarVisible(mMovieDetailLayoutBinding.movieDetailImageProgressBar);
                        Picasso
                                .get()
                                .load(ApiConstant.POSTER_PATH_BASE_URL +
                                        ApiConstant.POSTER_PATH_SIZE + imageUrl)
                                .error(R.drawable.image_url_broken)
                                .into(mMovieDetailLayoutBinding.movieDetailImageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        mMovieDetailLayoutBinding.movieDetailImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        setProgressBarGone(
                                                mMovieDetailLayoutBinding.movieDetailImageProgressBar);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e(TAG, "Picsso Loading Errror ------------- " + e.getMessage() + "********* ");
                                        mMovieDetailLayoutBinding.movieDetailImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                        setProgressBarGone(mMovieDetailLayoutBinding.movieDetailImageProgressBar);
                                    }
                                });
                    }
                });
    }

    /**
     * Sets the progressbar to visible
     *
     * @param progressBar the required progressbar
     */
    private void setProgressBarVisible(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the progressbar to gone
     *
     * @param progressBar the required progressbar
     */
    private void setProgressBarGone(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * Used for setting a darkTheme according to the user's choice
     */
    private void darkTheme() {
        //mMovieDetailLayoutBinding.movieDetailTitle.setTextColor(getColor(R.color.colorAppBackgroundLight));
        mMovieDetailLayoutBinding.movieDetailNestedScrollView.setBackgroundColor(getColor(R.color.colorAppBackgroundDark));
        mMovieDetailLayoutBinding.movieDetailStatus.setTextColor(getColor(R.color.colorAccentDark));
        mMovieDetailLayoutBinding.movieDetailStatusTv.setTextColor(getColor(R.color.colorAccent));
        mMovieDetailLayoutBinding.movieDetailReleaseDate.setTextColor(getColor(R.color.colorAccentDark));
        mMovieDetailLayoutBinding.movieDetailReleaseDateTv.setTextColor(getColor(R.color.colorAccent));
        mMovieDetailLayoutBinding.movieDetailSynopsisLabel.setTextColor(getColor(R.color.colorAccentDark));
        mMovieDetailLayoutBinding.movieDetailSynopsisTv.setTextColor(getColor(R.color.colorAccent));
        mMovieDetailLayoutBinding.movieDetailRatingLabel.setTextColor(getColor(R.color.colorAccentDark));
        mMovieDetailLayoutBinding.movieDetailRatingTv.setTextColor(getColor(R.color.colorAccent));
        mMovieDetailLayoutBinding.movieDetailSynopsisUnderlineView.setBackgroundColor(getColor(R.color.colorAccentDark));
        mMovieDetailLayoutBinding.movieDetailCastLabel.setTextColor(getColor(R.color.colorAccentDark));
        mMovieDetailLayoutBinding.movieDetailCastUnderlineView.setBackgroundColor(getColor(R.color.colorAccentDark));
        //mMovieDetailLayoutBinding.movieDetailRatingLabel.setTextColor(getColor(R.color.colorAccentDark));
    }

    /**
     * Used for setting a lightTheme according to the user's choice
     */
    private void lightTheme() {
        //mMovieDetailLayoutBinding.movieDetailTitle.setTextColor(getColor(R.color.colorAppBackgroundLight));
        mMovieDetailLayoutBinding.movieDetailNestedScrollView.setBackgroundColor(getColor(R.color.colorAppBackgroundLight));
        mMovieDetailLayoutBinding.movieDetailStatus.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailStatusTv.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailReleaseDate.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailReleaseDateTv.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailSynopsisLabel.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailSynopsisTv.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailRatingLabel.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailRatingTv.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailSynopsisUnderlineView.setBackgroundColor(getColor(R.color.colorDarkGray));
        //mMovieDetailLayoutBinding.movieDetailRatingLabel.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailCastLabel.setTextColor(getColor(R.color.colorDarkGray));
        mMovieDetailLayoutBinding.movieDetailCastUnderlineView.setBackgroundColor(getColor(R.color.colorDarkGray));
    }

    /**
     * This method is responsible for returning an int color value
     *
     * @param resource the color resource given
     * @return the color int value
     */
    private int getColor(@ColorRes int resource) {
        return ContextCompat.getColor(getContext(), resource);
    }
}
