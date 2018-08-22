package com.raphaelmarco.vianderito.network.service;

import com.raphaelmarco.vianderito.network.model.Paginated;
import com.raphaelmarco.vianderito.network.model.store.Product;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StoreService {

    @GET("store/products")
    Call<Paginated<Product>> getProducts(@Query("page") int page);

    @GET("store/products")
    Call<Paginated<Product>> getProducts(@Query("page") int page,
                                         @Query("per_page") int perPage);

    @GET("store/products")
    Call<Paginated<Product>> getProducts(@Query("page") int page,
                                         @Query("per_page") int perPage,
                                         @Query("name") String name,
                                         @Query("manufacturer_id") String manufacturerId,
                                         @Query("category_id") String categoryId,
                                         @Query("upc") String upc);

}
