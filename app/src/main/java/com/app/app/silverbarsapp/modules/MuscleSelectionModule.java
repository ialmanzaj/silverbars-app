package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 03/20/17.
 */

import com.app.app.silverbarsapp.interactors.MuscleSelectionInteractor;
import com.app.app.silverbarsapp.presenters.MuscleSelectionPresenter;
import com.app.app.silverbarsapp.viewsets.MuscleSelectionView;

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
public class MuscleSelectionModule {

    private MuscleSelectionView view;

    public MuscleSelectionModule(MuscleSelectionView view) {
        this.view = view;
    }

    @Provides
    public MuscleSelectionView provideView() {
        return view;
    }

    @Provides
    public MuscleSelectionPresenter providePresenter(MuscleSelectionView view, MuscleSelectionInteractor interactor) {
        return new MuscleSelectionPresenter(view, interactor);
    }
}
