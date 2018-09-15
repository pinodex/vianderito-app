package com.raphaelmarco.vianderito.network.model.password;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ResetResponse {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("token")
    private String token;

    @SerializedName("expires_at")
    private Date expiresAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
