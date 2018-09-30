package com.raphaelmarco.vianderito.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.network.RetrofitClient;

public abstract class AuthenticatedActivity extends AppCompatActivity implements
        RetrofitClient.OnUnauthorizedListener {

    @Override
    protected void onResume() {
        super.onResume();

        RetrofitClient.registerOnUnauthorizedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        RetrofitClient.unregisterOnUnauthorizedListener(this);
    }

    @Override
    public void onUnauthorizedListener() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.login_expired)
                .setMessage(R.string.login_expired_message)
                .setCancelable(false)
                .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showLoginScreen();
                    }
                });

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.create().show();
            }
        });
    }

    private void showLoginScreen() {
        Intent intent = new Intent(this, WelcomeActivity.class);

        startActivity(intent);

        finishAffinity();
    }
}
