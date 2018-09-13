package com.raphaelmarco.vianderito.network.model.auth;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.binding.PasswordChangeData;

public class PasswordChange {

    @SerializedName("current_password")
    private String currentPassword;

    @SerializedName("new_password")
    private String newPassword;

    @SerializedName("confirm_password")
    private String confirmPassword;

    public PasswordChange() { }

    public PasswordChange(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public PasswordChange(PasswordChangeData data) {
        setCurrentPassword(data.currentPassword.get());

        setNewPassword(data.newPassword.get());

        setConfirmPassword(data.confirmPassword.get());
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
