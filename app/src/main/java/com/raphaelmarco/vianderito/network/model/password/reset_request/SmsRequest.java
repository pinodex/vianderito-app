package com.raphaelmarco.vianderito.network.model.password.reset_request;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.binding.passwordreset.SmsRequestData;

public class SmsRequest {

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("code")
    private String code;

    public SmsRequest(String phoneNumber, String code) {
        this.phoneNumber = phoneNumber;
        this.code = code;
    }

    public SmsRequest(SmsRequestData data) {
        setPhoneNumber(data.phoneNumber.get());

        setCode(data.code.get());
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
