package com.app.app.silverbarsapp.viewsets;


import com.app.app.silverbarsapp.models.Person;

/**
 * Created by isaacalmanza on 04/04/17.
 */

public interface UserPreferencesView extends BaseView{
    //database
    void onProfileSaved(Person person);

    void onWorkoutSaved();
}
