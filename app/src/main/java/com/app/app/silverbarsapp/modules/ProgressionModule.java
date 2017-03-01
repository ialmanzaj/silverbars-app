package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/28/17.
 */

import com.app.app.silverbarsapp.interactors.ProgressionInteractor;
import com.app.app.silverbarsapp.presenters.ProgressionPresenter;
import com.app.app.silverbarsapp.viewsets.ProgressionView;

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
public class ProgressionModule {

    private ProgressionView view;

    public ProgressionModule(ProgressionView view) {
        this.view = view;
    }

    @Provides
    public ProgressionView provideView() {
        return view;
    }

    @Provides
    public ProgressionPresenter providePresenter(ProgressionView view, ProgressionInteractor interactor) {
        return new ProgressionPresenter(view, interactor);
    }
}
