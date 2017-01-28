package com.app.proj.silverbars.callbacks;

import com.app.proj.silverbars.models.AccessToken;

/**
 * Created by isaacalmanza on 01/28/17.
 */

public interface LoginCallback extends ServerCallback {

    void onToken(AccessToken accessToken);
}
