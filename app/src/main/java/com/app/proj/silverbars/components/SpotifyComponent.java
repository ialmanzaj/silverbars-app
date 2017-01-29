package com.app.proj.silverbars.components;

/**
 * Created by isaacalmanza on 01/28/17.
 */

import com.app.proj.silverbars.ActivityScope;
import com.app.proj.silverbars.activities.SpotifyActivity;
import com.app.proj.silverbars.modules.SpotifyModule;
import com.app.proj.silverbars.presenters.SpotifyPresenter;

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
        modules =  SpotifyModule.class
)
public interface SpotifyComponent {

    void inject(SpotifyActivity activity);

    SpotifyPresenter getPresenter();
}
