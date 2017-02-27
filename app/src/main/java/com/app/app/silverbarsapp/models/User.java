package com.app.app.silverbarsapp.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by isaacalmanza on 10/04/16.
 */

public class User {

    @SerializedName("id")
     private int id;

    @SerializedName("username")
     private String username;

    @SerializedName("first_name")
    private String first_name;

    @SerializedName("last_name")
    private String last_name;


    public int getId() {
        return id;
    }

    public String getUsername() {return username;}

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }


}
