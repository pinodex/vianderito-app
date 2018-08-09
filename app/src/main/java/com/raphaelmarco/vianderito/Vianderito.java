package com.raphaelmarco.vianderito;

import android.app.Application;
import android.content.ContextWrapper;
import android.util.Log;

import com.pixplicity.easyprefs.library.Prefs;
import com.raphaelmarco.vianderito.debug.AppSignatureHelper;

public class Vianderito extends Application {

    public static final String JWT_TOKEN_ID = "_api_jwt_token";

    @Override
    public void onCreate() {
        super.onCreate();

        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);

        Log.d("SIGN", appSignatureHelper.getAppSignatures().toString());
    }
}
