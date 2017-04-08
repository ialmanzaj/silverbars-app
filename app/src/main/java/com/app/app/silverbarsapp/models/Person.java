package com.app.app.silverbarsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by isaacalmanza on 11/16/16.
 */

public class Person implements Parcelable{

    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private User user;


    @SerializedName("age")
    private int age;


    @SerializedName("muscles")
    private List<Muscle> muscles;


    @SerializedName("my_workouts")
    private List<String> my_workouts;


    public Person(){}


    protected Person(Parcel in) {
        id = in.readInt();
        age = in.readInt();
        muscles = in.createTypedArrayList(Muscle.CREATOR);
        my_workouts = in.createStringArrayList();
    }

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel in) {
            return new Person(in);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public List<Muscle> getMuscles() {
        return muscles;
    }

    public List<String> getMy_workouts() {
        return my_workouts;
    }

    public User getUser() {
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(age);
        parcel.writeTypedList(muscles);
        parcel.writeStringList(my_workouts);
    }
}
