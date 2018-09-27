package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.cart.Purchase;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OrderService {

    @GET("orders")
    Call<ArrayList<Purchase>> getOrders();

    @GET("orders/{id}")
    Call<Purchase> getPurchase(@Path(value = "id", encoded =  true) String id);

}
