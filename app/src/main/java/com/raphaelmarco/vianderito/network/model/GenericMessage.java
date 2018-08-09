package com.raphaelmarco.vianderito.network.model;

import com.google.gson.annotations.SerializedName;

public class GenericMessage {

    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
