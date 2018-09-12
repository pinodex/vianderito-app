package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.Picture;
import com.raphaelmarco.vianderito.network.model.auth.User;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ProfileService {

    @GET("profile")
    Call<User> get();

    @PUT("profile")
    Call<ResponseBody> save(@Body User user);

    @GET("profile/picture")
    Call<Picture> getPicture();

    @POST("profile/picture")
    Call<Picture> setPicture(@Part MultipartBody.Part file);

}
