package com.app.app.silverbarsapp.viewsets;

import com.app.app.silverbarsapp.database_models.FbProfile;

import okhttp3.ResponseBody;

/**
 * Created by isaacalmanza on 02/26/17.
 */

public interface ProfileView {
    void displayProfileFacebook(FbProfile profile);
    void displayProfileImg(ResponseBody img);

}
