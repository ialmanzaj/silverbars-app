package com.app.app.silverbarsapp.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.app.silverbarsapp.R;

/**
 * Created by isaacalmanza on 05/25/17.
 */

public class CustomFilterView extends LinearLayout {

    TextView muscleTextView;

    public CustomFilterView(Context context) {
        super(context);
        init(context);
    }

    public CustomFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomFilterView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.filter_view, this);


        muscleTextView = (TextView) findViewById(R.id.muscle);
    }

    public void setMuscle(String muscle){
        muscleTextView.setText(muscle);
    }


}
