package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/10/17.
 */

import com.app.app.silverbarsapp.interactors.SavedWorkoutsInteractor;
import com.app.app.silverbarsapp.presenters.SavedWorkoutsPresenter;
import com.app.app.silverbarsapp.viewsets.SavedWorkoutsView;

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
public class SavedWorkoutsModule {

    private SavedWorkoutsView view;

    public SavedWorkoutsModule(SavedWorkoutsView view) {
        this.view = view;
    }

    @Provides
    public SavedWorkoutsView provideView() {
        return view;
    }

    @Provides
    public SavedWorkoutsPresenter providePresenter(SavedWorkoutsView view, SavedWorkoutsInteractor interactor) {
        return new SavedWorkoutsPresenter(view, interactor);
    }
    
    
    
    
}
