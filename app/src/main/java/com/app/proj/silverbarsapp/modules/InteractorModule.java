package com.app.proj.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */


import com.app.proj.silverbarsapp.LoginService;
import com.app.proj.silverbarsapp.MainService;
import com.app.proj.silverbarsapp.interactors.CreateWorkoutFinalInteractor;
import com.app.proj.silverbarsapp.interactors.CreateWorkoutInteractor;
import com.app.proj.silverbarsapp.interactors.ExerciseListInteractor;
import com.app.proj.silverbarsapp.interactors.LoginInteractor;
import com.app.proj.silverbarsapp.interactors.MainWorkoutsInteractor;
import com.app.proj.silverbarsapp.interactors.SavedWorkoutsInteractor;
import com.app.proj.silverbarsapp.interactors.SpotifyInteractor;
import com.app.proj.silverbarsapp.interactors.UserWorkoutsInteractor;
import com.app.proj.silverbarsapp.interactors.WorkoutInteractor;
import com.app.proj.silverbarsapp.utils.DatabaseHelper;

import dagger.Module;
import dagger.Provides;

/**
 * The module due is create objects to solve dependencies
 * trough methods annotated with {@link } annotation.
 *<p>
 * I use a Module based on this
 * <a href="http://frogermcs.github.io/dagger-1-to-2-migration/">tutorial</a>
 *</p>
 */

@Module
public class InteractorModule {

    @Provides
    public MainWorkoutsInteractor provideMainWorkoutsInteractor(MainService mainService){
        return new MainWorkoutsInteractor(mainService);
    }

    @Provides
    public CreateWorkoutInteractor provideCreateWorkoutInteractor(MainService mainService){
        return new CreateWorkoutInteractor(mainService);
    }

    @Provides
    public LoginInteractor provideLoginInteractor(LoginService loginService){
        return new LoginInteractor(loginService);
    }

    @Provides
    public SpotifyInteractor provideSpotifyInteractor(){
        return new SpotifyInteractor();
    }


    @Provides
    public WorkoutInteractor provideWorkoutInteractor(DatabaseHelper helper){
        return new WorkoutInteractor(helper);
    }

    @Provides
    public SavedWorkoutsInteractor provideSavedWorkoutsInteractor(DatabaseHelper helper){
        return new SavedWorkoutsInteractor(helper);
    }


    @Provides
    public ExerciseListInteractor provideExerciseListInteractor(MainService mainService){
        return new ExerciseListInteractor(mainService);
    }

    @Provides
    public CreateWorkoutFinalInteractor provideCreateWorkoutFinalInteractor(DatabaseHelper helper){
        return new CreateWorkoutFinalInteractor(helper);
    }

    @Provides
    public UserWorkoutsInteractor provideUserWorkoutsInteractor(DatabaseHelper helper){
        return new UserWorkoutsInteractor(helper);
    }





}
