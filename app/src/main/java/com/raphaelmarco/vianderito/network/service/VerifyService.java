package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.GenericMessage;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.model.verify.Verify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface VerifyService {

    @POST("verify/sms/start")
    Call<User> start(@Body User user);

    @POST("verify/sms/verify")
    Call<GenericMessage> verify(@Body Verify verify);

}
