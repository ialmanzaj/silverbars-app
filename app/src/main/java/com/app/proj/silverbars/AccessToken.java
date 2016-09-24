package com.app.proj.silverbars;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by isaacalmanza on 09/19/16.
 */
public class AccessToken implements Serializable{

    private static final String TAG = "AccessToken";

    @SerializedName("access_token")
    private String access_token;

    @SerializedName("token_type")
    private String token_type;

    @SerializedName("expires_in")
    private int expires_in;

    @SerializedName("refresh_token")
    private String refresh_token;

    @SerializedName("scope")
    private String scope;


    public AccessToken(String access_token,String token_type,int expires_in,String refresh_token,String scope){
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.scope = scope;
    }

    public String getAccess_token(){
        return access_token;
    }

    public String getRefresh_token(){
        return refresh_token;
    }

    public String getToken_type(){
        return token_type;
    }

    public int getExpires_in(){
        return expires_in;
    }

    public String getScope(){
        return scope;
    }


}
