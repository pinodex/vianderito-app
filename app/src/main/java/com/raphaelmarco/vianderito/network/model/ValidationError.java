package com.raphaelmarco.vianderito.network.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Response;

public class ValidationError {

    @SerializedName("message")
    private String message;

    @SerializedName("errors")
    private HashMap<String, String[]> errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, String[]> getErrors() {
        return errors;
    }

    public void setErrors(HashMap<String, String[]> errors) {
        this.errors = errors;
    }

    public static class Parser {

        private Response response;

        public Parser(Response response) {
            this.response = response;
        }

        public ValidationError parse() {
            Gson gson = new GsonBuilder().create();
            ValidationError error = new ValidationError();

            try {
                error = gson.fromJson(response.errorBody().string(), ValidationError.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return error;
        }

    }

}
