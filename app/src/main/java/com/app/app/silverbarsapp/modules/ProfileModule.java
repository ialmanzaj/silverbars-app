package com.app.app.silverbarsapp.modules;

/**
 * Created by isaacalmanza on 02/26/17.
 */

import com.app.app.silverbarsapp.interactors.ProfileInteractor;
import com.app.app.silverbarsapp.presenters.ProfilePresenter;
import com.app.app.silverbarsapp.viewsets.ProfileView;

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
public class ProfileModule {

    private ProfileView view;

    public ProfileModule(ProfileView view) {
        this.view = view;
    }

    @Provides
    public ProfileView provideView() {
        return view;
    }

    @Provides
    public ProfilePresenter providePresenter(ProfileView view, ProfileInteractor interactor) {
        return new ProfilePresenter(view, interactor);
    }

}
