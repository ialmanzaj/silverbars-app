package com.app.proj.silverbars;

/**
 * Created by isaacalmanza on 09/19/16.
 */
public class AccessToken {


    public String access_token;
    public String refresh_token;
    public String token_type;
    public int expires_in;

    public AccessToken(String access_token,String refresh_token,String token_type,int expires_in){
        this.access_token = access_token;
        this.refresh_token = refresh_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
    }

    public String getAccess_token(){
        return access_token;
    }
    public void setAccess_token(String access_token){
        this.access_token = access_token;
    }


}
