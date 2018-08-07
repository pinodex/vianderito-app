package com.raphaelmarco.vianderito.network;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static final String BASE_URL_PROD = "http://192.168.43.147:8000/api/v1/";

    public static final String BASE_URL_DEV = "http://10.0.2.2:8000/api/v1/";

    protected static Retrofit retrofit;

    public static Retrofit getInstance() {
        String base = BASE_URL_PROD;

        if (Build.FINGERPRINT.contains("generic")) {
            base = BASE_URL_DEV;
        }

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .setLenient()
                    .create();

            OkHttpClient httpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();

                    requestBuilder
                            .header("Content-Type", "application/json")
                            .header("Accept", "application/json");


                    return chain.proceed(requestBuilder.build());
                }
            }).build();

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .baseUrl(base)
                    .build();
        }

        return retrofit;
    }

}
