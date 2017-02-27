package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 01/30/17.
 */

import android.content.Context;

import com.app.app.silverbarsapp.presenters.WorkingOutPresenter;
import com.app.app.silverbarsapp.viewsets.WorkingOutView;

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
public class WorkingOutModule {

    private WorkingOutView view;

    public WorkingOutModule(WorkingOutView view) {
        this.view = view;
    }

    @Provides
    public WorkingOutView provideView() {
        return view;
    }

    @Provides
    public WorkingOutPresenter providePresenter(Context context, WorkingOutView view) {
        return new WorkingOutPresenter(context, view);
    }

}

