package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 05/17/17.
 */

public class ExerciseProgressionCompared extends Progression implements Parcelable{

    private ExerciseProgression exerciseProgression_old;
    private ExerciseProgression exerciseProgression_newer;
    private double progress;
    private String period_in_beetween;


    public ExerciseProgressionCompared( ExerciseProgression exerciseProgression_old,ExerciseProgression exerciseProgression_newer,String period_in_beetween){
        this.exerciseProgression_old = exerciseProgression_old;
        this.exerciseProgression_newer = exerciseProgression_newer;
        this.period_in_beetween = period_in_beetween;
    }


    protected ExerciseProgressionCompared(Parcel in) {
        super(in);
        exerciseProgression_old = in.readParcelable(ExerciseProgression.class.getClassLoader());
        exerciseProgression_newer = in.readParcelable(ExerciseProgression.class.getClassLoader());
        progress = in.readDouble();
        period_in_beetween = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(exerciseProgression_old, flags);
        dest.writeParcelable(exerciseProgression_newer, flags);
        dest.writeDouble(progress);
        dest.writeString(period_in_beetween);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExerciseProgressionCompared> CREATOR = new Creator<ExerciseProgressionCompared>() {
        @Override
        public ExerciseProgressionCompared createFromParcel(Parcel in) {
            return new ExerciseProgressionCompared(in);
        }

        @Override
        public ExerciseProgressionCompared[] newArray(int size) {
            return new ExerciseProgressionCompared[size];
        }
    };

    public String getPeriod_in_beetween() {
        return period_in_beetween;
    }

    public void setPeriod_in_beetween(String period_in_beetween) {
        this.period_in_beetween = period_in_beetween;
    }


    public ExerciseProgression getExerciseProgression_newer() {
        return exerciseProgression_newer;
    }

    public ExerciseProgression getExerciseProgression_old() {
        return exerciseProgression_old;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

}
