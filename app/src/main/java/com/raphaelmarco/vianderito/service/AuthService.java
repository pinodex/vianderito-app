package com.raphaelmarco.vianderito.service;

import com.raphaelmarco.vianderito.model.auth.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/register")
    Call<User> register(@Body User user);

}
