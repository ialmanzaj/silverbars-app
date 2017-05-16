package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 02/23/17.
 */
@DatabaseTable
public class FbProfile {

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private long id;

    @DatabaseField
    private String first_name;

    @DatabaseField
    private String last_name;

    @DatabaseField
    private String birthday;

    @DatabaseField
    private String gender;

    @DatabaseField
    private String email;


    public FbProfile(){}

    public FbProfile(long id, String first_name, String last_name, String birthday, String gender, String email) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String name) {
        this.first_name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }
}
