package com.app.proj.silverbarsapp.components;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.proj.silverbarsapp.ActivityScope;
import com.app.proj.silverbarsapp.activities.LoginActivity;
import com.app.proj.silverbarsapp.modules.LoginModule;
import com.app.proj.silverbarsapp.presenters.LoginPresenter;

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
