package com.example.android.popularmovies.utility;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class ReviewRecyclerView extends RecyclerView {

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
        heightSpec = MeasureSpec.makeMeasureSpec(((int) convertPxToDp(getContext(), 250)), MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }

    public float convertPxToDp(Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }
}
