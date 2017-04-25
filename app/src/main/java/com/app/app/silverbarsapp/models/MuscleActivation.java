package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by isaacalmanza on 04/23/17.
 */

public class MuscleActivation extends Progression implements Parcelable{

    private int muscle_activation;
    private double porcentaje;

    public MuscleActivation(){}

    protected MuscleActivation(Parcel in) {
        super(in);
        muscle_activation = in.readInt();
        porcentaje = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(muscle_activation);
        dest.writeDouble(porcentaje);
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

