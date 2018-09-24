package com.raphaelmarco.vianderito.network.model.gateway;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Payment {

    @SerializedName("id")
    public String id;

    @SerializedName("user_id")
    public String userId;

    @SerializedName("transaction_id")
    public String transactionId;

    @SerializedName("gateway_id")
    public String gatewayId;

    @SerializedName("customer_id")
    public String customerId;

    @SerializedName("status")
    public String status;

    @SerializedName("amount")
    public double amount;

    @SerializedName("created_at")
    public Date createdAt;

    @SerializedName("updated_at")
    public Date updatedAt;

}
