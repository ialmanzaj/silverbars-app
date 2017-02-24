package com.app.proj.silverbarsapp.callbacks;

import com.app.proj.silverbarsapp.models.AccessToken;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public interface LoginCallback extends ServerCallback {

    void onToken(AccessToken accessToken);
}
