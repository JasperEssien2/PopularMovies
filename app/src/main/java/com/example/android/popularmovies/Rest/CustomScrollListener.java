package com.example.android.popularmovies.Rest;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class CustomScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = CustomScrollListener.class.getSimpleName();
    private GridLayoutManager mGridLayoutManager;

    public CustomScrollListener(GridLayoutManager gridLayoutManager) {
        mGridLayoutManager = gridLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = mGridLayoutManager.getChildCount();
        int totalItemCount = mGridLayoutManager.getItemCount();
        int firstVisibleItemPosition = mGridLayoutManager.findFirstVisibleItemPosition();
        Log.e(TAG, "******* onScrolled ******** ");
        //if()
        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >=
                    totalItemCount && firstVisibleItemPosition >= 0) {
                loadMoreItems();
            }
        }
    }

    public abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}
