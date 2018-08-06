package com.raphaelmarco.vianderito.network;

import android.os.Build;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL_PROD = "http://192.168.43.147:8000/api/v1";

    public static final String BASE_URL_DEV = "http://10.0.2.2:8000/api/v1";

    protected static Retrofit retrofit;

    public static Retrofit getInstance() {
        String base = BASE_URL_PROD;

        if (Build.FINGERPRINT.contains("generic")) {
            base = BASE_URL_DEV;
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(base)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
