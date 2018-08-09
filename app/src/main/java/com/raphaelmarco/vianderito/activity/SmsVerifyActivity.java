package com.raphaelmarco.vianderito.activity;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.ActivitySmsVerifyBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.GenericMessage;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.model.verify.Verify;
import com.raphaelmarco.vianderito.network.service.VerifyService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SmsVerifyActivity extends AppCompatActivity {

    private User user;

    private VerifyService verifyService;

    private ProgressBar progressBar;

    private ValidationErrorData validationError;

    private UiData ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        {
            ui = new UiData();
            validationError = new ValidationErrorData();

            ActivitySmsVerifyBinding binding = DataBindingUtil.setContentView(
                    this, R.layout.activity_sms_verify);

            binding.setUi(ui);
            binding.setValidationError(validationError);
        }

        progressBar = findViewById(R.id.progress_bar);
        final EditText codeField = findViewById(R.id.sms_code);

        verifyService = RetrofitClient.getInstance().create(VerifyService.class);

        user = new Gson().fromJson(
                getIntent().getExtras().getString("user"), User.class);

        String verifyMessage = getResources().getString(R.string.verify_phone_message,
                user.getPhoneNumber());

        TextView verifyMessageView = findViewById(R.id.verify_phone_message);
        verifyMessageView.setText(verifyMessage);

        Button btnContinue = findViewById(R.id.btn_continue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLoadingState();

                String code = codeField.getText().toString();

                checkVerificationCode(code);
            }
        });

        TextView resendCodeLink = findViewById(R.id.resend_sms_link);

        resendCodeLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSmsVerification();
            }
        });

        startSmsVerification();
    }

    private void startSmsVerification() {
        enableLoadingState();

        verifyService.start(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                disableLoadingState();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();

                disableLoadingState();
            }
        });
    }

    private void checkVerificationCode(String code) {
        Verify verify = new Verify();
        verify.setCode(code);

        verifyService.verify(verify).enqueue(new Callback<GenericMessage>() {
            @Override
            public void onResponse(Call<GenericMessage> call, Response<GenericMessage> response) {
                disableLoadingState();

                if (response.isSuccessful()) {
                    Intent intent = new Intent(SmsVerifyActivity.this,
                            HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                    startActivity(intent);

                    finish();

                    return;
                }

                validationError.fill(new ValidationError.Parser(response).parse());
            }

            @Override
            public void onFailure(Call<GenericMessage> call, Throwable t) {
                t.printStackTrace();

                disableLoadingState();
            }
        });
    }

    private void enableLoadingState() {
        progressBar.setVisibility(View.VISIBLE);

        ui.isFormEnabled.set(false);
    }

    private void disableLoadingState() {
        progressBar.setVisibility(View.INVISIBLE);

        ui.isFormEnabled.set(true);
    }

    public class UiData extends BaseObservable {

        public ObservableField<Boolean> isFormEnabled = new ObservableField<>();

    }
}
