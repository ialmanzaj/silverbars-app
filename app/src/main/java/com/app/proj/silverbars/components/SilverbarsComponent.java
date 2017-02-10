package com.app.proj.silverbars.components;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import android.content.Context;

import com.app.proj.silverbars.interactors.CreateWorkoutInteractor;
import com.app.proj.silverbars.interactors.LoginInteractor;
import com.app.proj.silverbars.interactors.MainWorkoutsInteractor;
import com.app.proj.silverbars.interactors.SavedWorkoutsInteractor;
import com.app.proj.silverbars.interactors.SpotifyInteractor;
import com.app.proj.silverbars.interactors.WorkoutInteractor;
import com.app.proj.silverbars.modules.InteractorModule;
import com.app.proj.silverbars.modules.SilverbarsModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Pedro Antonio Hernández on 12/06/2015.
 *
 * <p>
 *     The component due is provide methods that the object graph can use to inject dependencies.
 *     Its like an API for our object graph. <br>
 *
 *     Those methods inject objects created on corresponding modules.
 * </p>
 */

@Singleton
@Component(
        modules = {
                SilverbarsModule.class,
                InteractorModule.class
        }
)
public interface SilverbarsComponent {

    Context getContext();

    MainWorkoutsInteractor getMainWorkoutsInteractor();

    CreateWorkoutInteractor getCreateWorkoutInteractor();

    LoginInteractor getLoginInteractor();

    SpotifyInteractor getSpotifyInteractor();

    WorkoutInteractor getWorkoutInteractor();

    SavedWorkoutsInteractor getSavedWorkoutsInteractor();

}
