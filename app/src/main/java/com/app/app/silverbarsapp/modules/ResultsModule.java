package com.app.app.silverbarsapp.modules;

import com.app.app.silverbarsapp.interactors.ResultsInteractor;
import com.app.app.silverbarsapp.presenters.ResultsPresenter;
import com.app.app.silverbarsapp.viewsets.ResultsView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by isaacalmanza on 03/18/17.
 */
@Module
public class ResultsModule {
    
    private ResultsView view;

    public ResultsModule(ResultsView view) {
        this.view = view;
    }

    @Provides
    public ResultsView provideView() {
        return view;
    }

    @Provides
    public ResultsPresenter providePresenter(ResultsView view, ResultsInteractor interactor) {
        return new ResultsPresenter(view, interactor);
    }

}
