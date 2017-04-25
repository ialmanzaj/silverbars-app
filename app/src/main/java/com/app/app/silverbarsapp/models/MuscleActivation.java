package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 04/23/17.
 */

public class MuscleActivation extends Progression implements Parcelable{

    private int muscle_activation;
    private double porcentaje;
    private String muscle_name;

    public MuscleActivation(){}


    protected MuscleActivation(Parcel in) {
        super(in);
        muscle_activation = in.readInt();
        porcentaje = in.readDouble();
        muscle_name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(muscle_activation);
        dest.writeDouble(porcentaje);
        dest.writeString(muscle_name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MuscleActivation> CREATOR = new Creator<MuscleActivation>() {
        @Override
        public MuscleActivation createFromParcel(Parcel in) {
            return new MuscleActivation(in);
        }

        @Override
        public MuscleActivation[] newArray(int size) {
            return new MuscleActivation[size];
        }
    };

    public String getMuscle_name() {
        return muscle_name;
    }

    public void setMuscle_name(String muscle_name) {
        this.muscle_name = muscle_name;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(double porcentaje) {
        this.porcentaje = porcentaje;
    }

    public int getMuscle_activation() {
        return muscle_activation;
    }

    public void setMuscle_activation(int muscle_activation) {
        this.muscle_activation = muscle_activation;
    }

}

