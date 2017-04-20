package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 04/18/17.
 */

import com.app.app.silverbarsapp.interactors.ExerciseDetailInteractor;
import com.app.app.silverbarsapp.presenters.ExerciseDetailPresenter;
import com.app.app.silverbarsapp.viewsets.ExerciseDetailView;

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
public class ExerciseDetailModule {

    private ExerciseDetailView view;

    public ExerciseDetailModule(ExerciseDetailView view) {
        this.view = view;
    }

    @Provides
    public ExerciseDetailView provideView() {
        return view;
    }

    @Provides
    public ExerciseDetailPresenter providePresenter(ExerciseDetailView view, ExerciseDetailInteractor interactor) {
        return new ExerciseDetailPresenter(view, interactor);
    }
}
