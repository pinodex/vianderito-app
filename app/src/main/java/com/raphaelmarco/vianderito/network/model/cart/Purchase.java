package com.raphaelmarco.vianderito.network.model.cart;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Purchase {

    @SerializedName("id")
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("transaction_id")
    public String transactionId;

    @SerializedName("gateway_id")
    public String gatewayId;

    @SerializedName("status")
    public String status;

    @SerializedName("amount")
    public double amount;

    @SerializedName("updated_at")
    public Date updatedAt;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("transaction")
    public Transaction transaction;

}
