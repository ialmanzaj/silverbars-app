package com.app.proj.silverbars.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class Exercise implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("exercise_name")
    private String exercise_name;

    @SerializedName("level")
    private String level;

    @SerializedName("type_exercise")
    private List<String> type_exercise;

    @SerializedName("exercise_audio")
    private String exercise_audio;

    @SerializedName("exercise_image")
    private String exercise_image;

    @SerializedName("muscles")
    private List<Muscle> muscles;


    public Exercise(){}



    public Exercise(int id,String exercise_name, String level, List<String> type_exercise,String exercise_audio,String exercise_image,List<Muscle> muscles){
        this.id = id;
        this.exercise_name = exercise_name;
        this.level = level;
        this.type_exercise = type_exercise;
        this.exercise_audio = exercise_audio;
        this.exercise_image = exercise_image;
        this.muscles = muscles;
    }




    protected Exercise(Parcel in) {
        id = in.readInt();
        exercise_name = in.readString();
        level = in.readString();
        type_exercise = in.createStringArrayList();
        exercise_audio = in.readString();
        exercise_image = in.readString();
        muscles = in.createTypedArrayList(Muscle.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(exercise_name);
        dest.writeString(level);
        dest.writeStringList(type_exercise);
        dest.writeString(exercise_audio);
        dest.writeString(exercise_image);
        dest.writeTypedList(muscles);
    }

    public static final Creator<Exercise> CREATOR = new Creator<Exercise>() {
        @Override
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        @Override
        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getExercise_audio() {
        return exercise_audio;
    }

    public void setExercise_audio(String exercise_audio) {
        this.exercise_audio = exercise_audio;
    }

    public String getExercise_image() {
        return exercise_image;
    }

    public void setExercise_image(String exercise_image) {
        this.exercise_image = exercise_image;
    }

    public List<Muscle> getMuscles() {
        return muscles;
    }


    public void setMuscles(List<Muscle> muscles) {
        this.muscles = muscles;
    }

    public List<String> getType_exercise() {
        return type_exercise;
    }

    public void setType_exercise(List<String> type_exercise) {
        this.type_exercise = type_exercise;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
