package com.example.android.popularmovies.Model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieResponse {

    @SerializedName("page")
    private int pageNumber;

    @SerializedName("results")
    private List<Movie> results;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("credits")
    private Movie.MovieCredits credit;

    @SerializedName("reviews")
    private Movie.MovieReviews reviews;

    @SerializedName("videos")
    private Movie.MovieTrailers trailers;

    @SerializedName("genres")
    private List<Movie.MovieGenre> genres;

    @SerializedName("status")
    private String status;

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<Movie> getResults() {
        return results;
    }

    public void setResults(List<Movie> results) {
        this.results = results;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public Movie.MovieCredits getCredit() {
        return credit;
    }

    public void setCredit(Movie.MovieCredits credit) {
        this.credit = credit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Movie.MovieTrailers getTrailers() {
        return trailers;
    }

    public void setTrailers(Movie.MovieTrailers trailers) {
        this.trailers = trailers;
    }

    public List<Movie.MovieGenre> getGenres() {
        return genres;
    }

    public void setGenres(List<Movie.MovieGenre> genres) {
        this.genres = genres;
    }

    public Movie.MovieReviews getReviews() {
        return reviews;
    }

    public void setReviews(Movie.MovieReviews reviews) {
        this.reviews = reviews;
    }

//    public List<String> getGenres() {
//        return genres;
//    }
//
//    public void setGenres(List<String> genres) {
//        this.genres = genres;
//    }

//    public static class MovieCastResponse{
//
//        @SerializedName("credits/cast")
//        private List<Movie.MovieCastCrew> credit;
//
//        @SerializedName("genres")
//        private List<String> genres;
//
//        public List<Movie.MovieCastCrew> getCredit() {
//            return credit;
//        }
//
//        public void setCredit(List<Movie.MovieCastCrew> credit) {
//            this.credit = credit;
//        }
//
//        public List<String> getGenres() {
//            return genres;
//        }
//
//        public void setGenres(List<String> genres) {
//            this.genres = genres;
//        }
//    }
}
