package com.example.android.popularmovies.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.example.android.popularmovies.R;

public class ExpandableTextView extends android.support.v7.widget.AppCompatTextView implements
        View.OnClickListener {
    private static final int MAX_LINES = 4;
    private int currentMaxLines = Integer.MAX_VALUE;
    private int icon = R.drawable.ic_more_text_dark;


    public ExpandableTextView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public ExpandableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    public void isDarkThemeSetting(boolean isDarkThemeSetting) {
        if (isDarkThemeSetting) icon = R.drawable.ic_more_text_dark;
        else icon = R.drawable.ic_more_text_light;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        post(new Runnable() {
            @Override
            public void run() {
                if (getLineCount() > MAX_LINES)
                    setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, icon);
                else setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                setMaxLines(MAX_LINES);
            }
        });
    }

    @Override
    public void setMaxLines(int maxLines) {
        currentMaxLines = maxLines;
        super.setMaxLines(maxLines);
    }

    public int getMaxLines() {
        return currentMaxLines;
    }

    @Override
    public void onClick(View view) {
        if (getMaxLines() == Integer.MAX_VALUE)
            setMaxLines(MAX_LINES);
        else setMaxLines(Integer.MAX_VALUE);
    }
}
