package com.example.android.popularmovies.Interfaces;

import com.example.android.popularmovies.Model.Movie;

import java.util.ArrayList;
import java.util.List;

public interface OnMovieItemSelected {

    /**
     * This method helps take care of what happens when an item is either selected or deselected
     *
     * @param position position of the item selected
     */
    void toggleSelection(int position);

    /**
     * this method helps clear any selections made when called
     */
    void clearSelections();

    /**
     * This is the number of items selected
     *
     * @return number of item selected
     */
    int getSelectedCount();

    /**
     * This method returns a list of instance Movie selected
     *
     * @return movie items
     */
    List<Movie> getSelectedItems();

    /**
     * This method returns a list of selected item positions
     *
     * @return selected item positions
     */
    ArrayList<Integer> getSelectedItemPosition();
}
