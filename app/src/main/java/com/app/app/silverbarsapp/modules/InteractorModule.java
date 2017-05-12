package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */


import com.app.app.silverbarsapp.LoginService;
import com.app.app.silverbarsapp.MainService;
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
import com.app.app.silverbarsapp.handlers.DatabaseHelper;

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
    public LoginInteractor provideLoginInteractor(LoginService loginService,DatabaseHelper helper){
        return new LoginInteractor(loginService,helper);
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
    public CreateWorkoutFinalInteractor provideCreateWorkoutFinalInteractor(DatabaseHelper helper,MainService mainService){
        return new CreateWorkoutFinalInteractor(helper,mainService);
    }

    @Provides
    public UserWorkoutsInteractor provideUserWorkoutsInteractor(MainService mainService,DatabaseHelper helper){
        return new UserWorkoutsInteractor(mainService,helper);
    }

    @Provides
    public ProfileInteractor provideProfileInteractor(DatabaseHelper helper){
        return new ProfileInteractor(helper);
    }

    @Provides
    public ProgressionInteractor provideProgressionInteractor(MainService mainService,DatabaseHelper helper){
        return new ProgressionInteractor(mainService,helper);
    }

    @Provides
    public ResultsInteractor provideResultsInteractor(DatabaseHelper helper,MainService mainService){
        return new ResultsInteractor(helper,mainService);
    }

    @Provides
    public MuscleSelectionInteractor provideMuscleSelectionInteractor(MainService mainService){
        return new MuscleSelectionInteractor(mainService);
    }

    @Provides
    public UserPreferencesInteractor provideUserPreferencesInteractor(DatabaseHelper databaseHelper,MainService mainService){
        return new UserPreferencesInteractor(databaseHelper,mainService);
    }

    @Provides
    public WorkoutsDoneInteractor provideWorkoutsDoneInteractor(DatabaseHelper databaseHelper, MainService mainService){
        return new WorkoutsDoneInteractor(databaseHelper,mainService);
    }

    @Provides
    public ExerciseDetailInteractor provideExerciseDetailInteractor(DatabaseHelper databaseHelper){
        return new ExerciseDetailInteractor(databaseHelper);
    }




}
