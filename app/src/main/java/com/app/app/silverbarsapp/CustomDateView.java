package com.app.app.silverbarsapp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Created by isaacalmanza on 09/23/16.
 */

public class CustomDateView extends LinearLayout {

    private TextView NameDay;
    private TextView NumberDay;


    public CustomDateView(Context context) {
        super(context);
        init(context);
    }

    public CustomDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomDateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.sample_custom_date_view, this);

        this.setOrientation(VERTICAL);
        /*this.setMinimumHeight(100);
        this.setMinimumWidth(50);*/


        NameDay = (TextView) findViewById(R.id.day);
        NumberDay = (TextView) findViewById(R.id.number);



    }

    public void setNameDay(String nameDay){
        NameDay.setText(nameDay);
    }

    public void setNumberDay(String numberDay) {
        NumberDay.setText(numberDay);
    }



}
