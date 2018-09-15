package com.raphaelmarco.vianderito.network.model.password;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.binding.passwordreset.ResetPasswordChangeData;

public class PasswordChange {

    @SerializedName("token")
    private String token;

    @SerializedName("new_password")
    private String newPassword;

    public PasswordChange(ResetPasswordChangeData data) {
        setToken(data.token.get());

        setNewPassword(data.newPassword.get());
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
