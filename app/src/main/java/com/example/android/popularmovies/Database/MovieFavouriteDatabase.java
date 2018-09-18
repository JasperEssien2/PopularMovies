package com.example.android.popularmovies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.android.popularmovies.Model.Movie;
import com.example.android.popularmovies.utility.MovieDao;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
//TODO: This line causes Databinding error
public abstract class MovieFavouriteDatabase extends RoomDatabase {
    private static MovieFavouriteDatabase movieFavouriteDatabase;

    public static MovieFavouriteDatabase getDatabase(Context context) {
        if (movieFavouriteDatabase == null) {
            movieFavouriteDatabase = Room
                    .databaseBuilder(context.getApplicationContext(),
                            MovieFavouriteDatabase.class,
                            "movie_db")
                    .build();
        }
        return movieFavouriteDatabase;
    }

    public abstract MovieDao movieDao();
}
