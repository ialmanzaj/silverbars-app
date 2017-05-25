package com.app.app.silverbarsapp.widgets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by isaacalmanza on 04/01/17.
 */

public class SeekbarWithIntervals extends LinearLayout {

    private LinearLayout mLinearLayout = null;
    private SeekBar mSeekBar = null;

    private int WidthMeasureSpec = 0;
    private int HeightMeasureSpec = 0;


    List<Integer> list_progress = new ArrayList<>();


    private Context context;

    public SeekbarWithIntervals(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        LayoutInflater.from(getContext()).inflate(R.layout.seekbar_with_intervals, this);
    }

    private Activity getActivity() {
        return (Activity) getContext();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (changed) {

            // We've changed the intervals layout, we need to refresh.
            mLinearLayout.measure(WidthMeasureSpec, HeightMeasureSpec);
            mLinearLayout.layout(mLinearLayout.getLeft(), mLinearLayout.getTop(), mLinearLayout.getRight(), mLinearLayout.getBottom());
        }
    }

    protected synchronized void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        WidthMeasureSpec = widthMeasureSpec;
        HeightMeasureSpec = heightMeasureSpec;

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getProgress() {
        return getSeekBar().getProgress();
    }

    public void setProgress(int progress) {
        getSeekBar().setProgress(progress);
        //changeTextColorSelected(progress);
    }

    public void setIntervals(List<String> intervals) {
        displayIntervals(intervals);
        getSeekBar().setMax(intervals.size() - 1);
    }

    public void setInitialProgress(int element){
        int a;
        do {
            a = (int) ((Math.random() * getMax()) + 1);
        } while (a == element);
        getSeekBar().setProgress(a);
    }

    public int getMax() {
        return getSeekBar().getMax();
    }

    private void displayIntervals(List<String> intervals) {
        if (getLinearLayout().getChildCount() == 0) {
            for (int a = 0;a<intervals.size();a++) {
                TextView textViewInterval = createInterval(intervals.get(a));

                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                param.weight = 1;

               if (a == 0){
                    textViewInterval.setGravity(Gravity.START);
                    param.setMargins(0, 0, (int) (getResources().getDimension(R.dimen.activity_horizontal_margin))*-1, 0);
                }

                getLinearLayout().addView(textViewInterval, param);
            }
        }
    }

    private TextView createInterval(String interval) {
        View layout = LayoutInflater.from(getContext()).inflate(R.layout.seekbar_with_intervals_labels,null);

        TextView textView = (TextView) layout.findViewById(R.id.textViewInterval);
        textView.setText(interval);

        return textView;
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener) {
        getSeekBar().setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    public void changeTextColorSelected(int selected){
        TextView view = (TextView) getLinearLayout().getChildAt(selected);
        view.setTextColor(Color.BLACK);
    }

    public void changeTextColorNoSelected(int last_selected){
        TextView view = (TextView) getLinearLayout().getChildAt(last_selected);
        view.setTextColor(getResources().getColor(R.color.gray_active_icon));
    }

    public void changeSeekBarTextViewColor(int progress){
        list_progress.add(progress);
        changeTextColorSelected(progress);
        if (list_progress.size() >= 2){
            int penultimo_element = list_progress.size()-2;
            changeTextColorNoSelected(list_progress.get(penultimo_element));
        }
    }

    private LinearLayout getLinearLayout() {
        if (mLinearLayout == null) {
            mLinearLayout = (LinearLayout) findViewById(R.id.intervals);
        }

        return mLinearLayout;
    }

    private SeekBar getSeekBar() {
        if (mSeekBar == null) {
            mSeekBar = (SeekBar) findViewById(R.id.seekbar);

            mSeekBar.setProgressDrawable(new ProgressDrawable(
                    mSeekBar.getProgressDrawable(),
                    mSeekBar,
                    getMax(),
                    ContextCompat.getColor(context,R.color.grey300))
            );

        }

        return mSeekBar;
    }
}