package com.app.app.silverbarsapp.components;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.app.silverbarsapp.ActivityScope;
import com.app.app.silverbarsapp.activities.LoginActivity;
import com.app.app.silverbarsapp.modules.LoginModule;
import com.app.app.silverbarsapp.presenters.LoginPresenter;

import dagger.Component;

/**
 * Created by Pedro Antonio Hernández on 13/06/2015.
 *
 * <p>
 *     Methods to inject dependencies into {@link }
 * </p>
 */

@ActivityScope
@Component(
        dependencies = SilverbarsComponent.class,
        modules =  LoginModule.class
)
public interface LoginComponent {

    void inject(LoginActivity loginActivity);

    LoginPresenter getPresenter();


}
