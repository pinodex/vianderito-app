package com.raphaelmarco.vianderito;

import android.app.Application;
import android.content.ContextWrapper;
import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;

public class Vianderito extends Application {

    public static final String JWT_TOKEN_ID = "_api_jwt_token";

    public static final String GATEWAY_TOKEN_ID = "_gateway_token";

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static void setToken(String token) {
        Prefs.putString(JWT_TOKEN_ID, token);
    }

    public static String getToken() {
        return Prefs.getString(JWT_TOKEN_ID, null);
    }

    public static void removeToken() {
        Prefs.remove(JWT_TOKEN_ID);
    }

    public static void setGatewayToken(String token) {
        Prefs.putString(GATEWAY_TOKEN_ID, token);
    }

    public static String getGatewayToken() {
        return Prefs.getString(GATEWAY_TOKEN_ID, null);
    }

    public static void removeGatewayToken() {
        Prefs.remove(GATEWAY_TOKEN_ID);
    }
}
