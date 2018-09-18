package com.example.android.popularmovies.utility;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.Model.Movie;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;
import static com.example.android.popularmovies.Constants.RoomConstants.MODEL_NAME;
import static com.example.android.popularmovies.Constants.RoomConstants.SELECT_FROM;
import static com.example.android.popularmovies.Constants.RoomConstants.WHERE_ID;

@Dao

public interface MovieDao {

    @Query(SELECT_FROM + MODEL_NAME)
    LiveData<List<Movie>> getAllMovies();

    @Query(SELECT_FROM + WHERE_ID)
    Movie getMovieById(int id);

    @Insert(onConflict = REPLACE)
    void addMovie(Movie movie);

    @Delete
    void deleteMovie(Movie movie);
}
