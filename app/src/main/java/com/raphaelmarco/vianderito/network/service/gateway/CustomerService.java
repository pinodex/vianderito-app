package com.raphaelmarco.vianderito.network.service.gateway;

import com.raphaelmarco.vianderito.network.model.gateway.Customer;
import com.raphaelmarco.vianderito.network.model.gateway.PaymentMethod;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CustomerService {

    @GET("gateway/customer")
    Call<ArrayList<Customer>> get();

    @GET("gateway/customer/{id}/details")
    Call<PaymentMethod> getDetails(@Path(value = "id", encoded =  true) String id);

    @POST("gateway/customer")
    Call<Customer> create(@Body PaymentMethod paymentMethod);

    @DELETE("gateway/customer/{id}")
    Call<ResponseBody> delete(@Path(value = "id", encoded =  true) String id);

}
