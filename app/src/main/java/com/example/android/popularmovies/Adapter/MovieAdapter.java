package com.example.android.popularmovies.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.Activity.HomeActivity;
import com.example.android.popularmovies.Constants.ApiConstant;
import com.example.android.popularmovies.Constants.BundleConstants;
import com.example.android.popularmovies.Fragments.MovieCastCrewDetailFrag;
import com.example.android.popularmovies.Fragments.MovieDetailBottomSheets;
import com.example.android.popularmovies.Interfaces.OnMovieItemSelected;
import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.Model.MovieResponse;
import com.example.android.popularmovies.R;
import com.example.android.popularmovies.Rest.ApiClient;
import com.example.android.popularmovies.Rest.ApiInterface;
import com.example.android.popularmovies.databinding.ActivityHomeBinding;
import com.example.android.popularmovies.databinding.ItemMovieBinding;
import com.example.android.popularmovies.databinding.ItemMovieReviewBinding;
import com.example.android.popularmovies.utility.ExpandableTextView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>
        implements OnMovieItemSelected {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private List<Movie> mMovies;
    private HomeActivity mActivity;
    private ActivityHomeBinding mActivityHomeBinding;
    private ItemMovieBinding mItemMovieBinding;
    private ActionModeViewCallBacks actionModeViewCallBacks;
    private SparseBooleanArray mSelectedItem = new SparseBooleanArray();
    private MovieViewHolder holder;
    private boolean isLoadingAdded = false;

    public MovieAdapter(List<Movie> movies, HomeActivity activity,
                        ActivityHomeBinding activityHomeBinding) {
        this.mMovies = movies;
        mActivity = activity;
        mActivityHomeBinding = activityHomeBinding;
    }

    public MovieAdapter() {
        super();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mItemMovieBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_movie, parent, false);
        return new MovieViewHolder(mItemMovieBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        this.holder = holder;
        bindViews(holder);
    }

    /**
     * A helper method that binds the view to its data
     *
     * @param holder
     */
    private void bindViews(MovieViewHolder holder) {
        Movie movie = mMovies.get(holder.getAdapterPosition());
        //mMovies.
        holder.movieName.setText(movie.getOriginalTitle());
        holder.movieRating.setText(String.valueOf(movie.getRating()));
        holder.moviePoster.setContentDescription(movie.getOriginalTitle());
        loadMovieImage(holder, movie.getMovieImageUrl());
        setActivated(holder.itemView, holder.getAdapterPosition());

    }

    /**
     * This helper method loads the imageUrl to the imageView
     *
     * @param holder   holder
     * @param imageUrl the image url
     */
    private void loadMovieImage(final MovieViewHolder holder, final String imageUrl) {
        setProgressBarVisible(holder.imageLoadingProgressbar);
        Picasso
                .get()
                .load(ApiConstant.POSTER_PATH_BASE_URL +
                        ApiConstant.POSTER_PATH_SIZE + imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.image_url_broken)
                .into(holder.moviePoster, new Callback() {
                    @Override
                    public void onSuccess() {
                        setProgressBarGone(holder.imageLoadingProgressbar);
                    }

                    @Override
                    public void onError(Exception e) {

                        Picasso
                                .get()
                                .load(ApiConstant.POSTER_PATH_BASE_URL +
                                        ApiConstant.POSTER_PATH_SIZE + imageUrl)
                                .error(R.drawable.image_url_broken)
                                .into(holder.moviePoster, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        setProgressBarGone(holder.imageLoadingProgressbar);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                        setProgressBarGone(holder.imageLoadingProgressbar);
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
     * This method initializes the ActionModeCallBacks
     *
     * @param callBacks
     */
    public void setActionListSelected(ActionModeViewCallBacks callBacks) {
        actionModeViewCallBacks = callBacks;
    }

    /**
     * When this method gets called it clears existing moving list
     */
    public void clearList() {
        isLoadingAdded = false;

        while (getItemCount() > 0) {
            removeItem(getItem(0));
        }
    }

    public boolean isMovieListEmpty() {
        return mMovies.size() == 0;
    }

    /**
     * A method that initializes the list of movies
     *
     * @param movieList a parameter for Datat type Movie list
     */
    public void addMovieList(List<Movie> movieList) {
        for (int i = 0; i < movieList.size(); i++) {
            add(movieList.get(i));
        }
    }

    public void updateIsLoadingTrue() {
        isLoadingAdded = true;
    }

    public void updateIsLoadingFalse() {
        isLoadingAdded = false;
    }

    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    public void add(Movie movie) {
        mMovies.add(movie);
        callMovieCast(movie.getId(), mMovies.indexOf(movie));
        notifyItemInserted(mMovies.size() - 1);
    }


    public void removeItem(Movie movie) {
        int pos = mMovies.indexOf(movie);
        if (pos > -1) {
            mMovies.remove(pos);
            notifyItemRemoved(pos);
        }
    }


    private void callMovieCast(int id, final int pos) {
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<MovieResponse> call = apiService
                .getMovieDetail(id, ApiConstant.API_KEY, ApiConstant.APPEND_ITEMS);
        call.enqueue(new retrofit2.Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                MovieResponse movieResponse = response.body();
                if (movieResponse == null) return;
                Movie.MovieCredits credit = movieResponse.getCredit();
                Movie.MovieTrailers trailers = movieResponse.getTrailers();
                List<Movie.MovieGenre> genres = movieResponse.getGenres();
                Movie.MovieReviews reviews = movieResponse.getReviews();

                if (pos < getItemCount()) {
                    mMovies.get(pos).setCasts(credit.getCasts());
                    mMovies.get(pos).setMovieStatus(movieResponse.getStatus());
                    mMovies.get(pos).setReviews(reviews.getReviews());
                    mMovies.get(pos).setTrailers(trailers.getTrailers());
                    mMovies.get(pos).setGenres(genres);
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
            }
        });
    }

    @Override
    public void toggleSelection(int position) {
        if (mSelectedItem.get(position)) {
            mSelectedItem.delete(position);
            mMovies.get(position).setSelected(false);
        } else {
            mSelectedItem.put(position, true);
            mMovies.get(position).setSelected(true);
        }
        notifyDataSetChanged();
    }

    @Override
    public void clearSelections() {
        hideSelectedIcon();
        mSelectedItem.clear();
        notifyDataSetChanged();
    }

    private void hideSelectedIcon() {
        for (Movie movie : getSelectedItems()) {
            movie.setSelected(false);
        }
    }

    @Override
    public int getSelectedCount() {
        return mSelectedItem.size();
    }

    @Override
    public ArrayList<Integer> getSelectedItemPosition() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int pos = 0; pos < mSelectedItem.size(); pos++) {
            arrayList.add(mSelectedItem.keyAt(pos));
        }
        return arrayList;
    }

    @Override
    public List<Movie> getSelectedItems() {
        List<Movie> movies = new LinkedList<>();
        for (int i = 0; i < mSelectedItem.size(); i++) {
            movies.add(mMovies.get(mSelectedItem.keyAt(i)));
        }
        return movies;
    }

    private void setActivated(View itemView, int pos) {
        if (mMovies.get(pos).isSelected())
            itemView.findViewById(R.id.image_view_checked_indicator).setVisibility(View.VISIBLE);
        else itemView.findViewById(R.id.image_view_checked_indicator).setVisibility(View.GONE);
        //TODO: HIDE OR SHOW CHECK IMAGEVIEW
    }

    @Override
    public int getItemCount() {
        if (mMovies == null) return 0;
        else return mMovies.size();
    }

    public interface ActionModeViewCallBacks {
        void onListItemSelect(final int position);

        void onDestroyActionMode();
    }

    public interface ListMovieActionModeViewCallbacks extends ActionModeViewCallBacks {
        void onShareActionButton();

        void onAddToStarredList();

        void onCancelActionButton();

        void onDeleteActionButton();
    }

    /**
     * MovieCastCrewAdapter is adapter for The casts or crew recyclerView
     */
    public static class MovieCastCrewAdapter extends RecyclerView.Adapter<MovieCastCrewAdapter.MovieCastViewHolder> {

        private List<Movie.MovieCastCrew> mCasts;
        private Context mContext;
        private FragmentManager mFragmentManager;
        private int mViewType;

        public MovieCastCrewAdapter(List<Movie.MovieCastCrew> casts, Context context,
                                    FragmentManager fragmentManager, int viewType) {
            super();
            mCasts = casts;
            mContext = context;
            mFragmentManager = fragmentManager;
            mViewType = viewType;
        }

        @NonNull
        @Override
        public MovieCastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            CircleImageView circleImageView = (CircleImageView)
                    LayoutInflater.from(mContext).inflate(R.layout.item_cast_image, parent, false);
            return new MovieCastViewHolder(circleImageView);
        }

        /**
         * This method is responsible for returning an int color value
         *
         * @param resource the color resource given
         * @return the color int value
         */
        private int getColor(@ColorRes int resource) {
            return ContextCompat.getColor(mContext, resource);
        }

        public void setList(List<Movie.MovieCastCrew> castCrews) {
            mCasts.clear();
            mCasts = castCrews;
            notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(@NonNull MovieCastViewHolder holder, int position) {
            holder.circleImageView.setContentDescription(mCasts.get(position).getCastName());
            loadMovieImage(holder, mCasts.get(holder.getAdapterPosition()).getProfileImagePath());
        }

        @Override
        public int getItemCount() {
            if (mCasts == null) {
                Toast.makeText(mContext, "Error showing cast", Toast.LENGTH_SHORT).show();
                return 0;
            }
            return mCasts.size();
        }

        /**
         * This helper method loads the imageUrl to the imageView
         *
         * @param imageUrl the image url
         */
        private void loadMovieImage(final MovieCastViewHolder holder, final String imageUrl) {
            //setProgressBarVisible(mMovieDetailLayoutBinding.movieDetailImageProgressBar);
            Picasso
                    .get()
                    .load(ApiConstant.POSTER_PATH_BASE_URL +
                            ApiConstant.POSTER_PATH_SIZE + imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .error(R.drawable.ic_profile_user)
                    .into(holder.circleImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso
                                    .get()
                                    .load(ApiConstant.POSTER_PATH_BASE_URL +
                                            ApiConstant.POSTER_PATH_SIZE + imageUrl)
                                    .error(R.drawable.ic_profile_user)
                                    .into(holder.circleImageView, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                        }
                                    });
                        }
                    });
        }

        public class MovieCastViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView circleImageView;

            public MovieCastViewHolder(View itemView) {
                super(itemView);
                circleImageView = (CircleImageView) itemView;
                circleImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(BundleConstants.MOVIE_CAST_CREW_DETAIL,
                                mCasts.get(getAdapterPosition()));
                        bundle.putInt(BundleConstants.MOVIE_CAST_CREW_DETAIL_TYPE, mViewType);
                        MovieCastCrewDetailFrag detailFrag = MovieCastCrewDetailFrag.newInstance(bundle);
                        detailFrag.show(mFragmentManager, BundleConstants.MOVIE_CAST_CREW_DETAIL);
                    }
                });
            }
        }
    }

    public static class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewViewHolder> {
        private List<Movie.MovieReview> reviews;
        private boolean isDarkThemeSetting;
        private Context context;
        private ItemMovieReviewBinding itemMovieReviewBinding;

        public MovieReviewsAdapter(List<Movie.MovieReview> reviews, boolean isDarkThemeSetting,
                                   Context context) {

            this.reviews = reviews;
            this.isDarkThemeSetting = isDarkThemeSetting;
            this.context = context;
        }

        @NonNull
        @Override
        public MovieReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            itemMovieReviewBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_movie_review, parent, false);
            return new MovieReviewViewHolder(itemMovieReviewBinding.getRoot());
        }

        @Override
        public void onBindViewHolder(@NonNull MovieReviewViewHolder holder, int position) {
            if (isDarkThemeSetting) itemDark(holder);
            else itemLight(holder);

            holder.reviewerName.setText(reviews.get(
                    holder.getAdapterPosition()).getReviewerName());
            holder.reviewContent.isDarkThemeSetting(isDarkThemeSetting);
            holder.reviewContent.setText(reviews.get(
                    holder.getAdapterPosition()).getReview().trim());

        }

        private void itemLight(MovieReviewViewHolder holder) {
            holder.itemView.setBackgroundColor(getColor(R.color.colorAppBackgroundLight));
            holder.reviewContent.setTextColor(getColor(R.color.colorDarkGray));
            holder.reviewerName.setTextColor(getColor(R.color.colorDarkGray));
        }

        private void itemDark(MovieReviewViewHolder holder) {
            holder.itemView.setBackgroundColor(getColor(R.color.colorAppBackgroundDark));
            holder.reviewContent.setTextColor(getColor(R.color.colorAccent));
            holder.reviewerName.setTextColor(getColor(R.color.colorAccentDark));
        }

        /**
         * This method is responsible for returning an int color value
         *
         * @param resource the color resource given
         * @return the color int value
         */
        private int getColor(@ColorRes int resource) {
            return ContextCompat.getColor(context, resource);
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        public class MovieReviewViewHolder extends RecyclerView.ViewHolder {
            private TextView reviewerName;
            private ExpandableTextView reviewContent;

            public MovieReviewViewHolder(View itemView) {
                super(itemView);
                initViews();
            }

            private void initViews() {
                reviewerName = itemMovieReviewBinding.reviewerName;
                reviewContent = itemMovieReviewBinding.reviewContent;
            }
        }
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private ImageView moviePoster;
        private TextView movieRating, movieName;
        private ProgressBar imageLoadingProgressbar;
        private MovieDetailBottomSheets bottomSheets;

        public MovieViewHolder(View itemView) {
            super(itemView);
            initViews();
        }

        /**
         * This helper method, initializes the item views
         */
        public void initViews() {
            moviePoster = mItemMovieBinding.imageMoviePoster;
            movieRating = mItemMovieBinding.textviewMovieRating;
            movieName = mItemMovieBinding.textviewMovieName;
            imageLoadingProgressbar = mItemMovieBinding.progressbarImageLoading;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mActivityHomeBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (bottomSheets != null)
                        bottomSheets.dismiss();
                }
            });
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.movie_item_view:
                    onItemClicked(view);
                    break;
            }
        }

        private void onItemClicked(View view) {
            if (mActivity != null && mActivity.mActionMode != null) {
                actionModeViewCallBacks.onListItemSelect(getAdapterPosition());
            } else {
//            TODO: SHOW MOVIE DETAILS USING BOTTOM SHEET
                Bundle bundle = bottomSheetArgs();
                if (bundle != null) {
                    bottomSheets = MovieDetailBottomSheets
                            .newInstance(bottomSheetArgs());
                    bottomSheets.show(mActivity.getSupportFragmentManager(),
                            BundleConstants.MOVIE_DETAIL_TAG);
                }
            }
        }

        /**
         * This method initialize a Bundle and put all necessary details to be transferred to the
         * ButtonSheetFragment
         *
         * @return the Bundle
         */
        private Bundle bottomSheetArgs() {
            if (getAdapterPosition() != -1) {
                Movie movie = mMovies.get(getAdapterPosition());
                Bundle args = new Bundle();
                args.putParcelable(BundleConstants.MOVIE_DETAIL_TAG, movie);
                return args;
            }
            return null;
        }

        @Override
        public boolean onLongClick(View view) {
            int id = view.getId();
            switch (id) {
                case R.id.movie_item_view:
                    actionModeViewCallBacks.onListItemSelect(getAdapterPosition());
                    return true;
            }
            return false;
        }
    }
}
