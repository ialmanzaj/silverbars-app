package com.app.app.silverbarsapp.callbacks;


/**
 * Created by isaacalmanza on 04/04/17.
 */

public interface UserPreferencesCallback extends ServerCallback {
    void onProfileSaved();
    void onWorkoutsSaved();
}
