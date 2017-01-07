package com.app.proj.silverbars.viewsets;

import com.app.proj.silverbars.models.AccessToken;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public interface LoginView extends BaseView{

    void displayToken(AccessToken accessToken);

}
