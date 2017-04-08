package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 04/07/17.
 */

import com.app.app.silverbarsapp.interactors.WorkoutsDoneInteractor;
import com.app.app.silverbarsapp.presenters.WorkoutsDonePresenter;
import com.app.app.silverbarsapp.viewsets.WorkoutsDoneView;

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
public class WorkoutsDoneModule {

    private WorkoutsDoneView view;

    public WorkoutsDoneModule(WorkoutsDoneView view) {
        this.view = view;
    }

    @Provides
    public WorkoutsDoneView provideView() {
        return view;
    }

    @Provides
    public WorkoutsDonePresenter providePresenter(WorkoutsDoneView view, WorkoutsDoneInteractor interactor) {
        return new WorkoutsDonePresenter(view, interactor);
    }



}
