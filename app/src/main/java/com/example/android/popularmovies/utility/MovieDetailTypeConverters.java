package com.example.android.popularmovies.utility;

import android.arch.persistence.room.TypeConverter;

import com.example.android.popularmovies.Model.Movie;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class MovieDetailTypeConverters {

    private static Gson gson = new Gson();

    @TypeConverter
    public static String castToString(List<Movie.MovieCastCrew> castCrewList) {
        return gson.toJson(castCrewList);
    }

    @TypeConverter
    public static List<Movie.MovieCastCrew> stringToCast(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Movie.MovieCastCrew>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String reviewsToString(List<Movie.MovieReview> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Movie.MovieReview> stringToReviews(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Movie.MovieReview>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String trailersToString(List<Movie.MovieTrailer> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Movie.MovieTrailer> stringToTrailer(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Movie.MovieTrailer>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }

    @TypeConverter
    public static String genreToString(List<Movie.MovieGenre> list) {
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Movie.MovieGenre> stringToGenre(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<Movie.MovieGenre>>() {
        }.getType();
        return gson.fromJson(data, listType);
    }
}
