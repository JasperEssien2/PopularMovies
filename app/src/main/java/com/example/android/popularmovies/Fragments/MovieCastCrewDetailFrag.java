package com.example.android.popularmovies.Fragments;

//import android.app.Dialog;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.popularmovies.Constants.ApiConstant;
import com.example.android.popularmovies.Constants.BundleConstants;
import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.SettingsSharedPreference;
import com.example.android.popularmovies.databinding.CastCrewDetailsBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MovieCastCrewDetailFrag extends DialogFragment {
    private static final String TAG = MovieCastCrewDetailFrag.class.getSimpleName();
    private CastCrewDetailsBinding mCastCrewDetailsBinding;

    public MovieCastCrewDetailFrag() {
        super();
    }

    /**
     * Creates a new insance of the fragment
     *
     * @param args
     * @return
     */
    public static MovieCastCrewDetailFrag newInstance(Bundle args) {
        MovieCastCrewDetailFrag fragment = new MovieCastCrewDetailFrag();
        //fragment.setStyle(MovieCastCrewDetailFrag.STYLE_NO_TITLE, 0);
        //fragment.setStyle(MovieCastCrewDetailFrag.STYLE_NO_FRAME, R.style.AppTheme_Translucent);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCastCrewDetailsBinding = DataBindingUtil
                .inflate(inflater, R.layout.cast_crew_details, container, false);
//        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppTheme_Translucent);
//        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppTheme_Translucent);
        getDialog().setCanceledOnTouchOutside(true);
        if (SettingsSharedPreference.getThemeSettings().equals(SettingsSharedPreference.THEME_SETTINGS_DARK))
            setDarkTheme();
        else if (SettingsSharedPreference.getThemeSettings().equals(SettingsSharedPreference.THEME_SETTINGS_LIGHT))
            setLightTheme();
        bindDataWithView();
        return mCastCrewDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * This method binds the data with the views
     */
    private void bindDataWithView() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(
                BundleConstants.MOVIE_CAST_CREW_DETAIL)) {
            int viewType = bundle.getInt(BundleConstants.MOVIE_CAST_CREW_DETAIL_TYPE);
            Movie.MovieCastCrew castCrew = (Movie.MovieCastCrew) bundle
                    .getParcelable(BundleConstants.MOVIE_CAST_CREW_DETAIL);
            if (castCrew == null) return;
            mCastCrewDetailsBinding.castCrewDetailsToolbar.castCrewDetailImage.setContentDescription(
                    castCrew.getCastName());
            loadMovieImage(castCrew.getProfileImagePath());
            mCastCrewDetailsBinding.castCrewGenderTv.setText(getGender(castCrew.getGender()));
            mCastCrewDetailsBinding.castCrewNameTv.setText(castCrew.getCastName());
            if (viewType == BundleConstants.MOVIE_CAST_DETAIL_TYPE) bindDataWithViewCast(castCrew);
            else if (viewType == BundleConstants.MOVIE_CREW_DETAIL_TYPE)
                bindDataWithViewCrew(castCrew);
        }
    }

    /**
     * This helper method loads the imageUrl to the imageView
     *
     * @param imageUrl the image url
     */
    private void loadMovieImage(final String imageUrl) {
        //setProgressBarVisible(mMovieDetailLayoutBinding.movieDetailImageProgressBar);
//        Log.e(TAG, "****** ------ " + ApiConstant.POSTER_PATH_BASE_URL +
//                ApiConstant.POSTER_PATH_SIZE + imageUrl + " ------ ********");
        Picasso
                .get()
                .load(ApiConstant.POSTER_PATH_BASE_URL +
                        ApiConstant.POSTER_PATH_SIZE + imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.image_url_broken)
                .into(mCastCrewDetailsBinding.castCrewDetailsToolbar.castCrewDetailImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Picsso Loading Errror ------------- " + e.getMessage() + "********* ");
                        Picasso
                                .get()
                                .load(ApiConstant.POSTER_PATH_BASE_URL +
                                        ApiConstant.POSTER_PATH_SIZE + imageUrl)
                                .error(R.drawable.image_url_broken)
                                .into(mCastCrewDetailsBinding.castCrewDetailsToolbar.castCrewDetailImage, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e(TAG, "Picsso Loading Errror ------------- " + e.getMessage() + "********* ");
                                    }
                                });
                    }
                });
    }

    /**
     * This method returns a string indicating gender depending on the value passed to it
     *
     * @param gender the int value
     * @return gender
     */
    private String getGender(int gender) {
        if (gender == 1)
            return "Female";
        else if (gender == 2)
            return "Male";
        else return "";
    }

    private void bindDataWithViewCast(Movie.MovieCastCrew castCrew) {
        mCastCrewDetailsBinding.castCharacterLabel.setVisibility(View.VISIBLE);
        mCastCrewDetailsBinding.castCharacterTv.setVisibility(View.VISIBLE);
        mCastCrewDetailsBinding.castCharacterTv.setText(castCrew.getCharacter());
        mCastCrewDetailsBinding.crewJobLabel.setVisibility(View.GONE);
        mCastCrewDetailsBinding.crewJobTv.setVisibility(View.GONE);
        mCastCrewDetailsBinding.crewDepartmentLabel.setVisibility(View.GONE);
        mCastCrewDetailsBinding.crewDepartmentTv.setVisibility(View.GONE);
    }

    private void bindDataWithViewCrew(Movie.MovieCastCrew castCrew) {
        mCastCrewDetailsBinding.crewJobLabel.setVisibility(View.VISIBLE);
        mCastCrewDetailsBinding.crewJobTv.setVisibility(View.VISIBLE);
        mCastCrewDetailsBinding.crewDepartmentLabel.setVisibility(View.VISIBLE);
        mCastCrewDetailsBinding.crewDepartmentTv.setVisibility(View.VISIBLE);
        mCastCrewDetailsBinding.castCharacterLabel.setVisibility(View.GONE);
        mCastCrewDetailsBinding.castCharacterTv.setVisibility(View.GONE);
        mCastCrewDetailsBinding.crewJobTv.setText(castCrew.getJob());
        mCastCrewDetailsBinding.crewDepartmentTv.setText(castCrew.getDepartment());

    }

    /**
     * Sets light theme
     */
    private void setLightTheme() {
        mCastCrewDetailsBinding.castCrewDetailNested.setBackgroundColor(getColor(R.color.colorAppBackgroundLight));
        setColor(mCastCrewDetailsBinding.castCrewName, true, true);
        setColor(mCastCrewDetailsBinding.castCrewNameTv, true, false);
        setColor(mCastCrewDetailsBinding.castCrewGenderLabel, true, true);
        setColor(mCastCrewDetailsBinding.castCrewGenderTv, true, false);
        setColor(mCastCrewDetailsBinding.castCharacterLabel, true, true);
        setColor(mCastCrewDetailsBinding.castCharacterTv, true, false);
        setColor(mCastCrewDetailsBinding.crewDepartmentLabel, true, true);
        setColor(mCastCrewDetailsBinding.crewDepartmentTv, true, false);
        setColor(mCastCrewDetailsBinding.crewJobLabel, true, true);
        setColor(mCastCrewDetailsBinding.crewJobTv, true, false);

//        RadioButton radioButton = new RadioButton(this);
//        radioButton.setT
    }

    /**
     * Sets dark theme
     */
    private void setDarkTheme() {
        mCastCrewDetailsBinding.castCrewDetailNested.setBackgroundColor(getColor(R.color.colorAppBackgroundDark));
        setColor(mCastCrewDetailsBinding.castCrewName, false, true);
        setColor(mCastCrewDetailsBinding.castCrewNameTv, false, false);
        setColor(mCastCrewDetailsBinding.castCrewGenderLabel, false, true);
        setColor(mCastCrewDetailsBinding.castCrewGenderTv, false, false);
        setColor(mCastCrewDetailsBinding.castCharacterLabel, false, true);
        setColor(mCastCrewDetailsBinding.castCharacterTv, false, false);
        setColor(mCastCrewDetailsBinding.crewDepartmentLabel, false, true);
        setColor(mCastCrewDetailsBinding.crewDepartmentTv, false, false);
        setColor(mCastCrewDetailsBinding.crewJobLabel, false, true);
        setColor(mCastCrewDetailsBinding.crewJobTv, false, false);
    }

    /**
     * Sets the color of the view provided
     *
     * @param view         the required view to set the color on
     * @param isLightTheme boolean indicating the theme (if light or dark theme)
     * @param isLabel      boolean indicating if the view passed is a label
     */
    private void setColor(View view, boolean isLightTheme, boolean isLabel) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (isLightTheme) textView.setTextColor(getColor(R.color.colorDarkGray));
            else {
                if (isLabel)
                    textView.setTextColor(getColor(R.color.colorAccentDark));
                else textView.setTextColor(getColor(R.color.colorAccent));
            }
        } else {
            if (isLightTheme)
                view.setBackgroundColor(getColor(R.color.colorAppBackgroundLight));
            else view.setBackgroundColor(getColor(R.color.colorAppBackgroundDark));
        }
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
