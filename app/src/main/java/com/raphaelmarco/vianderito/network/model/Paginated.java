package com.raphaelmarco.vianderito.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Paginated<T> {

    @SerializedName("current_page")
    private int currentPage;

    @SerializedName("first_page_url")
    private String firstPageUrl;

    @SerializedName("last_page_url")
    private String lastPageUrl;

    @SerializedName("prev_page_url")
    private String prevPageUrl;

    @SerializedName("next_page_url")
    private String nextPageUrl;

    @SerializedName("path")
    private String path;

    @SerializedName("from")
    private int from;

    @SerializedName("to")
    private int to;

    @SerializedName("per_page")
    private int perPage;

    @SerializedName("last_page")
    private int lastPage;

    @SerializedName("total")
    private int total;

    @SerializedName("data")
    private ArrayList<T> data;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getFirstPageUrl() {
        return firstPageUrl;
    }

    public void setFirstPageUrl(String firstPageUrl) {
        this.firstPageUrl = firstPageUrl;
    }

    public String getLastPageUrl() {
        return lastPageUrl;
    }

    public void setLastPageUrl(String lastPageUrl) {
        this.lastPageUrl = lastPageUrl;
    }

    public String getPrevPageUrl() {
        return prevPageUrl;
    }

    public void setPrevPageUrl(String prevPageUrl) {
        this.prevPageUrl = prevPageUrl;
    }

    public String getNextPageUrl() {
        return nextPageUrl;
    }

    public void setNextPageUrl(String nextPageUrl) {
        this.nextPageUrl = nextPageUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }
}
