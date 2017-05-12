package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.database_models.ProfileFacebook;
import com.app.app.silverbarsapp.models.AccessToken;

/**
 * Created by isaacalmanza on 01/07/17.
 */

public interface LoginView extends BaseView{
    void displayToken(AccessToken accessToken);
    void onProfileSaved(ProfileFacebook profile);
}
