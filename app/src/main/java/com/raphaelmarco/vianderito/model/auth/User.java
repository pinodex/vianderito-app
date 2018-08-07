package com.raphaelmarco.vianderito.model.auth;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.binding.SignUpData;

import java.util.Date;

public class User {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("email_address")
    private String emailAddress;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    public User(SignUpData data) {
        this.setName(data.name.get());
        this.setUsername(data.username.get());
        this.setPassword(data.password.get());
        this.setPhoneNumber(data.phoneNumber.get());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
