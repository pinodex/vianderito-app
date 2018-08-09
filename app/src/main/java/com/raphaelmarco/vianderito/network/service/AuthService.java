package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.model.auth.UserCredentials;
import com.raphaelmarco.vianderito.network.model.auth.UserLogin;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {

    @POST("auth/login")
    Call<UserLogin> login(@Body UserCredentials userCredentials);

    @POST("auth/register")
    Call<User> register(@Body User user);

}
