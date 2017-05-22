package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 05/19/17.
 */

public class MuscleActivationCompared extends Progression implements Parcelable {

    private int old_muscle_activation;
    private int current_muscle_activation;

    private int muscle_activation_average;
    private double progress;
    private String period_in_beetween;

    private String muscle_name;

    public MuscleActivationCompared(){}



    public MuscleActivationCompared(int old_muscle_activation,int current_muscle_activation,String muscle_name){
        this.old_muscle_activation = old_muscle_activation;
        this.current_muscle_activation = current_muscle_activation;
        this.muscle_name = muscle_name;
    }


    protected MuscleActivationCompared(Parcel in) {
        super(in);
        old_muscle_activation = in.readInt();
        current_muscle_activation = in.readInt();
        muscle_activation_average = in.readInt();
        progress = in.readDouble();
        period_in_beetween = in.readString();
        muscle_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(old_muscle_activation);
        dest.writeInt(current_muscle_activation);
        dest.writeInt(muscle_activation_average);
        dest.writeDouble(progress);
        dest.writeString(period_in_beetween);
        dest.writeString(muscle_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MuscleActivationCompared> CREATOR = new Creator<MuscleActivationCompared>() {
        @Override
        public MuscleActivationCompared createFromParcel(Parcel in) {
            return new MuscleActivationCompared(in);
        }

        @Override
        public MuscleActivationCompared[] newArray(int size) {
            return new MuscleActivationCompared[size];
        }
    };

    public void setMuscle_activation_average(int muscle_activation_average) {
        this.muscle_activation_average = muscle_activation_average;
    }

    public void setPeriod_in_beetween(String period_in_beetween) {
        this.period_in_beetween = period_in_beetween;
    }

    public void setMuscle_name(String muscle_name) {
        this.muscle_name = muscle_name;
    }

    public int getMuscle_activation_average() {
        return muscle_activation_average;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public int getCurrent_muscle_activation() {
        return current_muscle_activation;
    }

    public int getOld_muscle_activation() {
        return old_muscle_activation;
    }

    public String getMuscle_name() {
        return muscle_name;
    }

    public String getPeriod_in_beetween() {
        return period_in_beetween;
    }
}
