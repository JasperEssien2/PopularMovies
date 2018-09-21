package com.example.android.popularmovies.utility;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ReviewRecyclerView extends RecyclerView {

    private static final String TAG = ReviewRecyclerView.class.getSimpleName();

    public ReviewRecyclerView(Context context) {
        super(context);
    }

    public ReviewRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ReviewRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = MeasureSpec.makeMeasureSpec(measureDimension(widthSpec, 350), MeasureSpec.AT_MOST);

        setMeasuredDimension(widthSpec, heightSpec);
        super.onMeasure(widthSpec, heightSpec);
    }

    private int measureDimension(int widthSpec, int heightMeasureSpec) {
        int desiredHeight = 350;
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height;

        height = Math.min(desiredHeight, heightSize);
        return height;
    }
}
