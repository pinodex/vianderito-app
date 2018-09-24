package com.raphaelmarco.vianderito.network.model.gateway;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.binding.billing.CustomerData;

public class PaymentMethod {

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("address")
    private String address;

    @SerializedName("city")
    private String city;

    @SerializedName("state")
    private String state;

    @SerializedName("country")
    private String country;

    @SerializedName("postal_code")
    private String postalCode;

    @SerializedName("nonce")
    private String nonce;

    @SerializedName("type")
    private String type;

    @SerializedName("last_four")
    private String lastFour;

    @SerializedName("expiration_month")
    private String expirationMonth;

    @SerializedName("expiration_year")
    private String expirationYear;

    public void fill(CustomerData data) {
        setFirstName(data.firstName.get());

        setLastName(data.lastName.get());

        setAddress(data.address.get());

        setCity(data.city.get());

        setState(data.state.get());

        setCountry(data.country.get());

        setPostalCode(data.postalCode.get());
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLastFour() {
        return lastFour;
    }

    public void setLastFour(String lastFour) {
        this.lastFour = lastFour;
    }

    public String getExpirationMonth() {
        return expirationMonth;
    }

    public void setExpirationMonth(String expirationMonth) {
        this.expirationMonth = expirationMonth;
    }

    public String getExpirationYear() {
        return expirationYear;
    }

    public void setExpirationYear(String expirationYear) {
        this.expirationYear = expirationYear;
    }
}
