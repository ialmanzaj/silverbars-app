package com.app.app.silverbarsapp.database_models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by isaacalmanza on 02/23/17.
 */
@DatabaseTable
public class ProfileFacebook {

    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private long id;

    @DatabaseField
    private String first_name;

    @DatabaseField
    private String last_name;


    public ProfileFacebook(){}


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
}
