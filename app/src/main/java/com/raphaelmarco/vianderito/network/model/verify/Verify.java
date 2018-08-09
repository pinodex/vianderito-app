package com.raphaelmarco.vianderito.network.model.verify;

import com.google.gson.annotations.SerializedName;

public class Verify {

    @SerializedName("code")
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
