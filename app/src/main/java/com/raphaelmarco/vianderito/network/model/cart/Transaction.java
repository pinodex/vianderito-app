package com.raphaelmarco.vianderito.network.model.cart;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.network.model.store.Inventory;

import java.util.ArrayList;
import java.util.Date;

public class Transaction {

    @SerializedName("id")
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("status")
    public String status;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("updated_at")
    public Date updatedAt;

    @SerializedName("inventories")
    public ArrayList<Inventory> inventories;

    @SerializedName("total")
    public double total;

}
