package com.raphaelmarco.vianderito.network.model.store;

import com.google.gson.annotations.SerializedName;
import com.raphaelmarco.vianderito.network.model.Picture;

import java.util.Date;

public class Product {

    @SerializedName("id")
    private String id;

    @SerializedName("manufacturer_id")
    private String manufacturerId;

    @SerializedName("category_id")
    private String categoryId;

    @SerializedName("upc")
    private String upc;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private Date createdAt;

    @SerializedName("updated_at")
    private Date updatedAt;

    @SerializedName("picture")
    private Picture picture;

    @SerializedName("front_inventory")
    private Inventory frontInventory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(String manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getUpc() {
        return upc;
    }

    public void setUpc(String upc) {
        this.upc = upc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Inventory getFrontInventory() {
        return frontInventory;
    }

    public void setFrontInventory(Inventory frontInventory) {
        this.frontInventory = frontInventory;
    }
}
