package com.raphaelmarco.vianderito.network.service.gateway;

import com.raphaelmarco.vianderito.network.model.gateway.Payment;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PaymentService {

    @POST("gateway/payment")
    Call<Payment> create(@Body Payment payment);

}
