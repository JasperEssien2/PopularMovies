package com.example.android.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    private boolean isSelected;
    //@SerializedName("cast")
    private List<MovieCastCrew> casts;

    public Movie(String originalTitle, String movieImageUrl, String plotSynopsis, double rating,
                 String releaseDate, int id, List<MovieCastCrew> casts, boolean isSelected) {

        this.originalTitle = originalTitle;
        this.movieImageUrl = movieImageUrl;
        this.plotSynopsis = plotSynopsis;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.movieStatus = movieStatus;
        this.id = id;
        this.casts = casts;
        this.isSelected = isSelected;
    }

    protected Movie(Parcel in) {
        originalTitle = in.readString();
        movieImageUrl = in.readString();
        plotSynopsis = in.readString();
        rating = in.readDouble();
        releaseDate = in.readString();
        movieStatus = in.readString();
        id = in.readInt();
        isSelected = in.readByte() != 0;
        casts = in.createTypedArrayList(MovieCastCrew.CREATOR);
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
        parcel.writeByte((byte) (isSelected ? 1 : 0));
        parcel.writeTypedList(casts);
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
        @SerializedName("status")
        private String movieStatus;

        public MovieCredits(List<MovieCastCrew> crews, List<MovieCastCrew> casts, String movieStatus) {

            this.crews = crews;
            this.casts = casts;
            this.movieStatus = movieStatus;
        }

        protected MovieCredits(Parcel in) {
            crews = in.createTypedArrayList(MovieCastCrew.CREATOR);
            casts = in.createTypedArrayList(MovieCastCrew.CREATOR);
            movieStatus = in.readString();
        }

        public String getMovieStatus() {
            return movieStatus;
        }

        public void setMovieStatus(String movieStatus) {
            this.movieStatus = movieStatus;
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
            parcel.writeString(movieStatus);
        }

        @Override
        public String toString() {
            return "Cast: " + casts.toString() + "\n" +
                    "Crews: " + crews.toString() + "\n\n";
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

    public static class MovieGenre{

        private List<String> genres;

        public MovieGenre(){

        }

        public MovieGenre(List<String> genres){

            this.genres = genres;
        }

        public List<String> getGenres() {
            return genres;
        }

        public void setGenres(List<String> genres) {
            this.genres = genres;
        }
    }

}
