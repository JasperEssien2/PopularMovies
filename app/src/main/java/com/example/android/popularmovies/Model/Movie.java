package com.example.android.popularmovies.Model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class Movie implements Parcelable {

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    @SerializedName("original_title")
    private String originalTitle;
    @SerializedName("poster_path")
    private String movieImageUrl;
    @SerializedName("overview")
    private String plotSynopsis;
    @SerializedName("vote_average")
    private double rating;
    @SerializedName("release_date")
    private String releaseDate;
    //@SerializedName("status")
    private String movieStatus;
    @SerializedName("id")
    private int id;
    //    @PrimaryKey(autoGenerate = true)
    @PrimaryKey(autoGenerate = true)
    private int dataId;
    private boolean isSelected;

    private List<MovieCastCrew> casts;

    private List<MovieReview> reviews;

    private List<MovieTrailer> trailers;

    private List<MovieGenre> genres;

    public Movie(String originalTitle, String movieImageUrl, String plotSynopsis, double rating,
                 String releaseDate, int id, int dataId, List<MovieCastCrew> casts, boolean isSelected,
                 String movieStatus, List<MovieReview> reviews, List<MovieTrailer> trailers, List<MovieGenre> genres) {

        this.originalTitle = originalTitle;
        this.movieImageUrl = movieImageUrl;
        this.plotSynopsis = plotSynopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.dataId = dataId;
        this.movieStatus = movieStatus;
        this.id = id;
        this.casts = casts;
        this.isSelected = isSelected;
        this.reviews = reviews;
        this.trailers = trailers;
        this.genres = genres;
    }

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        movieImageUrl = in.readString();
        plotSynopsis = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
        movieStatus = in.readString();
        id = in.readInt();
        dataId = in.readInt();
        isSelected = in.readByte() != 0;
        casts = in.createTypedArrayList(MovieCastCrew.CREATOR);
        reviews = in.createTypedArrayList(MovieReview.CREATOR);
        trailers = in.createTypedArrayList(MovieTrailer.CREATOR);
        genres = in.createTypedArrayList(MovieGenre.CREATOR);
    }

    public Movie() {

    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getMovieImageUrl() {
        return movieImageUrl;
    }

    public void setMovieImageUrl(String movieImageUrl) {
        this.movieImageUrl = movieImageUrl;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public void setPlotSynopsis(String plotSynopsis) {
        this.plotSynopsis = plotSynopsis;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getMovieStatus() {
        return movieStatus;
    }

    public void setMovieStatus(String movieStatus) {
        this.movieStatus = movieStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public List<MovieCastCrew> getCasts() {
        return casts;
    }

    public void setCasts(List<MovieCastCrew> casts) {
        this.casts = casts;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(originalTitle);
        parcel.writeString(movieImageUrl);
        parcel.writeString(plotSynopsis);
        parcel.writeDouble(rating);
        parcel.writeString(releaseDate);
        parcel.writeString(movieStatus);
        parcel.writeInt(id);
        parcel.writeInt(dataId);
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeTypedList(casts);
        parcel.writeTypedList(reviews);
        parcel.writeTypedList(trailers);
        parcel.writeTypedList(genres);
    }

    public List<MovieReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<MovieReview> reviews) {
        this.reviews = reviews;
    }

    public List<MovieTrailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<MovieTrailer> trailers) {
        this.trailers = trailers;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<MovieGenre> genres) {
        this.genres = genres;
    }

    public static class MovieCredits implements Parcelable {

        public static final Creator<MovieCredits> CREATOR = new Creator<MovieCredits>() {
            @Override
            public MovieCredits createFromParcel(Parcel in) {
                return new MovieCredits(in);
            }

            @Override
            public MovieCredits[] newArray(int size) {
                return new MovieCredits[size];
            }
        };
        @SerializedName("crew")
        private List<MovieCastCrew> crews;
        @SerializedName("cast")
        private List<MovieCastCrew> casts;

        public MovieCredits(List<MovieCastCrew> crews, List<MovieCastCrew> casts) {

            this.crews = crews;
            this.casts = casts;
        }

        protected MovieCredits(Parcel in) {
            crews = in.createTypedArrayList(MovieCastCrew.CREATOR);
            casts = in.createTypedArrayList(MovieCastCrew.CREATOR);
        }

        public List<MovieCastCrew> getCrews() {
            return crews;
        }

        public void setCrews(List<MovieCastCrew> crews) {
            this.crews = crews;
        }

        public List<MovieCastCrew> getCasts() {
            return casts;
        }

        public void setCasts(List<MovieCastCrew> casts) {
            this.casts = casts;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeTypedList(crews);
            parcel.writeTypedList(casts);
        }

        @Override
        public String toString() {
            return "Cast: " + casts.toString() + "\n" +
                    "Crews: " + crews.toString() + "\n\n";
        }
    }

    public static class MovieReviews implements Parcelable {
        @SerializedName("results")
        private List<MovieReview> reviews;

        public MovieReviews(List<MovieReview> reviews) {

            this.reviews = reviews;
        }

        protected MovieReviews(Parcel in) {
            reviews = in.createTypedArrayList(MovieReview.CREATOR);
        }

        public static final Creator<MovieReviews> CREATOR = new Creator<MovieReviews>() {
            @Override
            public MovieReviews createFromParcel(Parcel in) {
                return new MovieReviews(in);
            }

            @Override
            public MovieReviews[] newArray(int size) {
                return new MovieReviews[size];
            }
        };

        public List<MovieReview> getReviews() {
            return reviews;
        }

        public void setReviews(List<MovieReview> reviews) {
            this.reviews = reviews;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeTypedList(reviews);
        }
    }

    public static class MovieTrailers implements Parcelable {

        @SerializedName("results")
        private List<MovieTrailer> trailers;

        public MovieTrailers(List<MovieTrailer> trailers) {

            this.trailers = trailers;
        }

        protected MovieTrailers(Parcel in) {
            trailers = in.createTypedArrayList(MovieTrailer.CREATOR);
        }

        public static final Creator<MovieTrailers> CREATOR = new Creator<MovieTrailers>() {
            @Override
            public MovieTrailers createFromParcel(Parcel in) {
                return new MovieTrailers(in);
            }

            @Override
            public MovieTrailers[] newArray(int size) {
                return new MovieTrailers[size];
            }
        };

        public List<MovieTrailer> getTrailers() {
            return trailers;
        }

        public void setTrailers(List<MovieTrailer> trailers) {
            this.trailers = trailers;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeTypedList(trailers);
        }
    }

    public static class MovieCastCrew implements Parcelable {

        public static final Creator<MovieCastCrew> CREATOR = new Creator<MovieCastCrew>() {
            @Override
            public MovieCastCrew createFromParcel(Parcel in) {
                return new MovieCastCrew(in);
            }

            @Override
            public MovieCastCrew[] newArray(int size) {
                return new MovieCastCrew[size];
            }
        };
        @SerializedName("gender")
        private int gender;
        @SerializedName("department")
        private String department;
        @SerializedName("job")
        private String job;
        @SerializedName("name")
        private String castName;
        @SerializedName("profile_path")
        private String profileImagePath;
        @SerializedName("character")
        private String character;

        public MovieCastCrew(String castName, String profileImagePath, String character, int gender,
                             String department, String job) {

            this.castName = castName;
            this.profileImagePath = profileImagePath;
            this.character = character;
            this.gender = gender;
            this.department = department;
            this.job = job;
        }

        protected MovieCastCrew(Parcel in) {
            castName = in.readString();
            profileImagePath = in.readString();
            character = in.readString();
            gender = in.readInt();
            department = in.readString();
            job = in.readString();
        }

        public String getCastName() {
            return castName;
        }

        public void setCastName(String castName) {
            this.castName = castName;
        }

        public String getProfileImagePath() {
            return profileImagePath;
        }

        public void setProfileImagePath(String profileImagePath) {
            this.profileImagePath = profileImagePath;
        }

        public String getCharacter() {
            return character;
        }

        public void setCharacter(String character) {
            this.character = character;
        }

        public int getGender() {
            return gender;
        }

        public String getDepartment() {
            return department;
        }

        public String getJob() {
            return job;
        }


        @Override
        public String toString() {
            return "castName: " + castName + "\n" +
                    "profileImagePath: " + profileImagePath + "\n" +
                    "character: " + character + "\n" +
                    "gender: " + gender + "\n" +
                    "department: " + department + "\n" +
                    "job: " + job + "\n\n";
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(castName);
            parcel.writeString(profileImagePath);
            parcel.writeString(character);
            parcel.writeInt(gender);
            parcel.writeString(department);
            parcel.writeString(job);
        }
    }

    public static class MovieGenre implements Parcelable {

        @SerializedName("name")
        private String genre;

        public MovieGenre(String genre) {

            this.genre = genre;
        }

        protected MovieGenre(Parcel in) {
            genre = in.readString();
        }

        public static final Creator<MovieGenre> CREATOR = new Creator<MovieGenre>() {
            @Override
            public MovieGenre createFromParcel(Parcel in) {
                return new MovieGenre(in);
            }

            @Override
            public MovieGenre[] newArray(int size) {
                return new MovieGenre[size];
            }
        };

        public String getGenre() {
            return genre;
        }

        public void setGenre(String genre) {
            this.genre = genre;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(genre);
        }
    }

    public static class MovieTrailer implements Parcelable {
        @SerializedName("key")
        private String trailerLink;

        public MovieTrailer(String trailerLink) {

            this.trailerLink = trailerLink;
        }

        protected MovieTrailer(Parcel in) {
            trailerLink = in.readString();
        }

        public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
            @Override
            public MovieTrailer createFromParcel(Parcel in) {
                return new MovieTrailer(in);
            }

            @Override
            public MovieTrailer[] newArray(int size) {
                return new MovieTrailer[size];
            }
        };

        public String getTrailerId() {
            return trailerLink;
        }

        public void setTrailerLink(String trailerLink) {
            this.trailerLink = trailerLink;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(trailerLink);
        }
    }

    public static class MovieReview implements Parcelable {
        @SerializedName("author")
        private String reviewerName;

        @SerializedName("content")
        private String review;

        protected MovieReview(Parcel in) {
            reviewerName = in.readString();
            review = in.readString();
        }

        public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
            @Override
            public MovieReview createFromParcel(Parcel in) {
                return new MovieReview(in);
            }

            @Override
            public MovieReview[] newArray(int size) {
                return new MovieReview[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(reviewerName);
            parcel.writeString(review);
        }

        public String getReviewerName() {
            return reviewerName;
        }

        public void setReviewerName(String reviewerName) {
            this.reviewerName = reviewerName;
        }

        public String getReview() {
            return review;
        }

        public void setReview(String review) {
            this.review = review;
        }
    }

}
