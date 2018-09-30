package com.raphaelmarco.vianderito.network.model.cart;

import com.google.gson.annotations.SerializedName;

public class Coupon {

    @SerializedName("id")
    public String id;

    @SerializedName("code")
    public String code;

    @SerializedName("discount_type")
    public String discountType;

    @SerializedName("discount_price")
    public String discountPrice;

    @SerializedName("discount_percentage")
    public String discountPercentage;

}
