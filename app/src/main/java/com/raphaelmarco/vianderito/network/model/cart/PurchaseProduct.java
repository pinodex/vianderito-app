package com.raphaelmarco.vianderito.network.model.cart;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.network.model.store.Product;

import java.util.Date;

public class PurchaseProduct {

    @SerializedName("id")
    public String id;

    @SerializedName("purchase_id")
    public String purchaseId;

    @SerializedName("product_id")
    public String productId;

    @SerializedName("name")
    public String name;

    @SerializedName("upc")
    public String upc;

    @SerializedName("price")
    public double price;

    @SerializedName("quantity")
    public int quantity;

    @SerializedName("subtotal")
    public double subTotal;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("updated_at")
    public Date updatedAt;

    @SerializedName("product")
    public Product product;

}
