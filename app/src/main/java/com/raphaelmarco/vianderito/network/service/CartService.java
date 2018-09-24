package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.cart.Transaction;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CartService {

    @GET("cart/transactions/{id}")
    Call<Transaction> get(@Path(value = "id", encoded =  true) String id);

    @DELETE("cart/transactions/{id}")
    Call<ResponseBody> delete(@Path(value = "id", encoded =  true) String id);

}
