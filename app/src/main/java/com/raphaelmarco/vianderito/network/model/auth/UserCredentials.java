package com.raphaelmarco.vianderito.network.model.auth;

import com.google.gson.annotations.SerializedName;

public class UserCredentials {

    @SerializedName("id")
    private String id;

    @SerializedName("password")
    private String password;

    public UserCredentials(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
