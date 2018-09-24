package com.raphaelmarco.vianderito.network.service.gateway;

import com.raphaelmarco.vianderito.network.model.gateway.Customer;
import com.raphaelmarco.vianderito.network.model.gateway.PaymentMethod;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CustomerService {

    @GET("gateway/customer")
    Call<ArrayList<Customer>> get();

    @POST("gateway/customer")
    Call<Customer> create(@Body PaymentMethod paymentMethod);

}
