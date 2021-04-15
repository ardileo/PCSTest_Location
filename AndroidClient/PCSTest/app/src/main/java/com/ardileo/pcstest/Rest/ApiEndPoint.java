package com.ardileo.pcstest.Rest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiEndPoint {

    @FormUrlEncoded
    @POST("/login")
    Call<ResponseBody> signIn(@Field("email") String email,
                              @Field("password") String password);

    @FormUrlEncoded
    @POST("/register")
    Call<ResponseBody> signUp(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("/location/add")
    Call<ResponseBody> saveLocation(@Field("latitude") double lat,
                                    @Field("longitude") double lon
    );

    @FormUrlEncoded
    @POST("location/delete")
    Call<ResponseBody> deleteLocation(@Field("id") int id);

    @GET("/locations")
    Call<ResponseBody> getLocations();

    @FormUrlEncoded
    @POST("/update/name")
    Call<ResponseBody> changeName(@Field("name") String name);
}
