package com.raphaelmarco.vianderito.network.model.password.reset_request;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.binding.passwordreset.EmailRequestData;
import com.raphaelmarco.vianderito.binding.passwordreset.SmsRequestData;

public class EmailRequest {

    @SerializedName("email_address")
    private String emailAddress;

    public EmailRequest(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public EmailRequest(EmailRequestData data) {
        setEmailAddress(data.emailAddress.get());
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
