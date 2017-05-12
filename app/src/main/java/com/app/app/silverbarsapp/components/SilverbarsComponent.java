package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import android.content.Context;

import com.app.app.silverbarsapp.interactors.CreateWorkoutFinalInteractor;
import com.app.app.silverbarsapp.interactors.CreateWorkoutInteractor;
import com.app.app.silverbarsapp.interactors.ExerciseDetailInteractor;
import com.app.app.silverbarsapp.interactors.ExerciseListInteractor;
import com.app.app.silverbarsapp.interactors.LoginInteractor;
import com.app.app.silverbarsapp.interactors.MainWorkoutsInteractor;
import com.app.app.silverbarsapp.interactors.MuscleSelectionInteractor;
import com.app.app.silverbarsapp.interactors.ProfileInteractor;
import com.app.app.silverbarsapp.interactors.ProgressionInteractor;
import com.app.app.silverbarsapp.interactors.ResultsInteractor;
import com.app.app.silverbarsapp.interactors.SavedWorkoutsInteractor;
import com.app.app.silverbarsapp.interactors.UserPreferencesInteractor;
import com.app.app.silverbarsapp.interactors.UserWorkoutsInteractor;
import com.app.app.silverbarsapp.interactors.WorkoutInteractor;
import com.app.app.silverbarsapp.interactors.WorkoutsDoneInteractor;
import com.app.app.silverbarsapp.modules.InteractorModule;
import com.app.app.silverbarsapp.modules.SilverbarsModule;

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

    WorkoutInteractor getWorkoutInteractor();

    SavedWorkoutsInteractor getSavedWorkoutsInteractor();

    ExerciseListInteractor getExerciseListInteractor();

    CreateWorkoutFinalInteractor getCreateWorkoutFinalInteractor();

    UserWorkoutsInteractor getUserWorkoutsInteractor();

    ProfileInteractor getProfileInteractor();

    ProgressionInteractor getProgressionInteractor();

    ResultsInteractor getResultsInteractor();

    MuscleSelectionInteractor getMuscleSelectionInteractor();

    UserPreferencesInteractor getUserPreferencesInteractor();

    WorkoutsDoneInteractor getWorkoutsDoneInteractor();

    ExerciseDetailInteractor getExerciseDetailInteractor();
}
