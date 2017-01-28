package com.app.proj.silverbars.modules;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.proj.silverbars.interactors.LoginInteractor;
import com.app.proj.silverbars.presenters.LoginPresenter;
import com.app.proj.silverbars.viewsets.LoginView;

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
public class LoginModule {

    private LoginView view;

    public LoginModule(LoginView view) {
        this.view = view;
    }

    @Provides
    public LoginView provideView() {
        return view;
    }

    @Provides
    public LoginPresenter providePresenter(LoginView view, LoginInteractor interactor) {
        return new LoginPresenter(view, interactor);
    }
    
    
}
