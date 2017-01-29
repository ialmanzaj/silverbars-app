package com.app.proj.silverbars.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */


import com.app.proj.silverbars.LoginService;
import com.app.proj.silverbars.MainService;
import com.app.proj.silverbars.interactors.CreateWorkoutInteractor;
import com.app.proj.silverbars.interactors.LoginInteractor;
import com.app.proj.silverbars.interactors.MainWorkoutsInteractor;
import com.app.proj.silverbars.interactors.SpotifyInteractor;

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


}
