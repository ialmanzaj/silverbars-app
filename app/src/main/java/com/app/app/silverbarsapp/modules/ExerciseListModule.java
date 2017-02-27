package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/13/17.
 */

import com.app.app.silverbarsapp.interactors.ExerciseListInteractor;
import com.app.app.silverbarsapp.presenters.ExerciseListPresenter;
import com.app.app.silverbarsapp.viewsets.ExerciseListView;

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
public class ExerciseListModule {

    private ExerciseListView view;

    public ExerciseListModule(ExerciseListView view) {
        this.view = view;
    }

    @Provides
    public ExerciseListView provideView() {
        return view;
    }

    @Provides
    public ExerciseListPresenter providePresenter(ExerciseListView view, ExerciseListInteractor interactor) {
        return new ExerciseListPresenter(view, interactor);
    }
}
