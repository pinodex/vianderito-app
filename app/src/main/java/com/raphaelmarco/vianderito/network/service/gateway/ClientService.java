package com.raphaelmarco.vianderito.network.service.gateway;

import com.raphaelmarco.vianderito.network.model.gateway.Token;

import retrofit2.Call;
import retrofit2.http.POST;

public interface ClientService {

    @POST("gateway/client/token")
    Call<Token> obtainToken();

}
