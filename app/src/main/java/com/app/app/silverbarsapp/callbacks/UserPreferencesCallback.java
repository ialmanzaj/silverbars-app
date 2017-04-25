package com.app.app.silverbarsapp.callbacks;


import com.app.app.silverbarsapp.models.Person;

/**
 * Created by isaacalmanza on 04/04/17.
 */

public interface UserPreferencesCallback extends ServerCallback {
    void onProfileSaved(Person person);
    void onWorkoutsSaved();
}
