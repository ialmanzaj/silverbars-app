package com.app.app.silverbarsapp.callbacks;

import com.app.app.silverbarsapp.database_models.ProfileFacebook;

import okhttp3.ResponseBody;

/**
 * Created by isaacalmanza on 02/26/17.
 */

public interface ProfileCallback {
    void getProfileFacebook(ProfileFacebook profile);
    void getProfileImg(ResponseBody img);
}
