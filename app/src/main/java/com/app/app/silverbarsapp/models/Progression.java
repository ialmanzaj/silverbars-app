package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 04/23/17.
 */

public abstract class Progression implements Parcelable {

    private boolean isEqual;
    private boolean isPositive;
    private boolean isNegative;

    private boolean isRepImprove;
    private boolean isSecondImprove;
    private boolean isWeightImprove;


    protected Progression(Parcel in) {
        isEqual = in.readByte() != 0;
        isPositive = in.readByte() != 0;
        isNegative = in.readByte() != 0;
        isRepImprove = in.readByte() != 0;
        isSecondImprove = in.readByte() != 0;
        isWeightImprove = in.readByte() != 0;
    }

    protected Progression() {}

    public void setNegative(boolean negative) {
        isNegative = negative;
    }

    public void setRepImprove(boolean repImprove) {
        isRepImprove = repImprove;
    }

    public void setSecondImprove(boolean secondImprove) {
        isSecondImprove = secondImprove;
    }

    public void setWeightImprove(boolean weightImprove) {
        isWeightImprove = weightImprove;
    }

    public boolean isSecondImprove() {
        return isSecondImprove;
    }

    public boolean isWeightImprove() {
        return isWeightImprove;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public boolean isRepImprove() {
        return isRepImprove;
    }

    public void setEqual(boolean equal) {
        isEqual = equal;
    }

    public void setPositive(boolean positive) {
        isPositive = positive;
    }

    public boolean isPositive() {
        return isPositive;
    }

    public boolean isEqual() {
        return isEqual;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isEqual ? 1 : 0));
        dest.writeByte((byte) (isPositive ? 1 : 0));
        dest.writeByte((byte) (isNegative ? 1 : 0));
        dest.writeByte((byte) (isRepImprove ? 1 : 0));
        dest.writeByte((byte) (isSecondImprove ? 1 : 0));
        dest.writeByte((byte) (isWeightImprove ? 1 : 0));
    }
}
