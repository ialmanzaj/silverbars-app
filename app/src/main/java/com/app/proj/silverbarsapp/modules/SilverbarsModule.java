package com.app.proj.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import android.app.Application;
import android.content.Context;

import com.app.proj.silverbarsapp.LoginService;
import com.app.proj.silverbarsapp.MainService;
import com.app.proj.silverbarsapp.ServiceGenerator;
import com.app.proj.silverbarsapp.SilverbarsApp;
import com.app.proj.silverbarsapp.utils.DatabaseHelper;

import javax.inject.Singleton;

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
public class SilverbarsModule {

    private SilverbarsApp app;

    public SilverbarsModule(SilverbarsApp app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return app;
    }


    @Provides
    @Singleton
    public MainService provideMainService(){
        return ServiceGenerator.createService(MainService.class);
    }


    @Provides
    @Singleton
    public LoginService provideLoginService(){
        return ServiceGenerator.createService(LoginService.class);
    }

    @Provides
    @Singleton
    public DatabaseHelper provideDatabaseHelper(Context context){
        return new DatabaseHelper(context);
    }





}
