package com.app.proj.silverbars;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
/**
 * Created by isaacalmanza on 10/04/16.
 */
public interface LoginService {

    @FormUrlEncoded
    @POST("auth/convert-token/")
    Call<AccessToken> getAccessToken(
            @Field("grant_type") String grant_type,
            @Field("client_id") String grantType,
            @Field("client_secret") String client_secret,
            @Field("backend") String backend,
            @Field("token") String token
            );

    @FormUrlEncoded
    @POST("auth/token/")
    Call<AccessToken> getRefreshAccessToken(
            @Field("grant_type") String grant_type,
            @Field("client_id") String client_id,
            @Field("client_secret") String client_secret,
            @Field("refresh_token") String refresh_token
    );

}

