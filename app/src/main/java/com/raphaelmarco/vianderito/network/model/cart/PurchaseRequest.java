package com.raphaelmarco.vianderito.network.model.cart;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.network.model.gateway.Payment;

public class PurchaseRequest {

    @SerializedName("payment_id")
    public String paymentId;

    public PurchaseRequest() {}

    public PurchaseRequest(Payment payment) {
        this.paymentId = payment.id;
    }

}
