package com.app.proj.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/03/17.
 */

import com.app.proj.silverbarsapp.interactors.WorkoutInteractor;
import com.app.proj.silverbarsapp.presenters.WorkoutPresenter;
import com.app.proj.silverbarsapp.viewsets.WorkoutView;

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
public class WorkoutModule {

    private WorkoutView view;

    public WorkoutModule(WorkoutView view) {
        this.view = view;
    }

    @Provides
    public WorkoutView provideView() {
        return view;
    }

    @Provides
    public WorkoutPresenter providePresenter(WorkoutView view,WorkoutInteractor interactor) {
        return new WorkoutPresenter(view, interactor);
    }

}
