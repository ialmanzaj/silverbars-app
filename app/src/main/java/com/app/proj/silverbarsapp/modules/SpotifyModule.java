package com.app.proj.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.proj.silverbarsapp.interactors.SpotifyInteractor;
import com.app.proj.silverbarsapp.presenters.SpotifyPresenter;
import com.app.proj.silverbarsapp.viewsets.SpotifyView;

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
public class SpotifyModule {


    private SpotifyView view;

    public SpotifyModule(SpotifyView view) {
        this.view = view;
    }

    @Provides
    public SpotifyView provideView() {
        return view;
    }

    @Provides
    public SpotifyPresenter providePresenter(SpotifyView view, SpotifyInteractor interactor) {
        return new SpotifyPresenter(view, interactor);
    }



    
}
