package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.GenericMessage;
import com.raphaelmarco.vianderito.network.model.cart.Coupon;
import com.raphaelmarco.vianderito.network.model.cart.Purchase;
import com.raphaelmarco.vianderito.network.model.cart.PurchaseRequest;
import com.raphaelmarco.vianderito.network.model.cart.Transaction;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CartService {

    @GET("cart/transactions/{id}")
    Call<Transaction> get(@Path(value = "id", encoded =  true) String id);

    @DELETE("cart/transactions/{id}")
    Call<ResponseBody> delete(@Path(value = "id", encoded =  true) String id);

    @POST("cart/transactions/{id}/purchase")
    Call<Purchase> purchase(@Path(value = "id", encoded =  true) String id,
                            @Body PurchaseRequest purchaseRequest);

    @POST("cart/transactions/{id}/coupon")
    Call<GenericMessage> setCoupon(@Path(value = "id", encoded =  true) String id,
                                     @Body Coupon coupon);

    @DELETE("cart/transactions/{id}/coupon")
    Call<GenericMessage> removeCoupon(@Path(value = "id", encoded =  true) String id);

}
