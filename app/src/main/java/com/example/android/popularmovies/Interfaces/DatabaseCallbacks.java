package com.example.android.popularmovies.Interfaces;

/**
 * When making a request to Room database, this interface contains callback methods to execute
 * when asynctask finishes execution
 */
public interface DatabaseCallbacks {

    /**
     * Called when item is successfully deleted
     */
    void itemDeletedSuccessfully();

    /**
     * Called when item is successfully inserted
     */
    void itemInsertedSuccessfully();

    /**
     * Called when request to check if item is exist in database is done executing
     *
     * @param addItem       boolean to indicate if the motive of the check is to add items to the database
     * @param isBottomSheet boolean to indicate is request to check is being called from moviebottomsheet
     * @param doItemExists  boolean to indicate if item exist in the database or not
     */
    void itemAlreadyExistInDatabase(boolean addItem, boolean isBottomSheet, boolean doItemExists);

    void allItemSuccessfulyInserted();

    void dismissLoadingSnackbar();
}
