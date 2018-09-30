package com.raphaelmarco.vianderito.activity.password;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.binding.passwordreset.EmailRequestData;
import com.raphaelmarco.vianderito.binding.passwordreset.SmsRequestData;
import com.raphaelmarco.vianderito.databinding.ActivityPasswordResetBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.password.ResetResponse;
import com.raphaelmarco.vianderito.network.model.password.reset_request.EmailRequest;
import com.raphaelmarco.vianderito.network.model.password.reset_request.SmsRequest;
import com.raphaelmarco.vianderito.network.service.password.ResetService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordResetActivity extends AppCompatActivity {

    public static final int RESET_SMS = 1;

    public static final int RESET_EMAIL = 2;

    private UiData ui = new UiData();

    private SmsRequestData smsRequestData = new SmsRequestData();

    private EmailRequestData emailRequestData = new EmailRequestData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private ResetService resetService;

    public PasswordResetActivity() {
        resetService = RetrofitClient.getInstance().create(ResetService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPasswordResetBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_password_reset);

        binding.setUi(ui);
        binding.setValidationError(validationError);
        binding.setSms(smsRequestData);
        binding.setEmail(emailRequestData);

        findViewById(R.id.tab_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.activeTab.set(RESET_SMS);
                validationError.clear();
            }
        });

        findViewById(R.id.tab_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.activeTab.set(RESET_EMAIL);
                validationError.clear();
            }
        });

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ui.isSmsVerification.get()) {
                    getResetTokenWithSms();

                    return;
                }

                switch (ui.activeTab.get()) {
                    case RESET_SMS: startSmsPasswordResetRequest(); break;
                    case RESET_EMAIL: startEmailPasswordResetRequest(); break;
                }
            }
        });
    }

    private void startSmsPasswordResetRequest() {
        ui.isLoading.set(true);
        validationError.clear();

        SmsRequest data = new SmsRequest(smsRequestData);

        resetService.startSms(data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ui.isLoading.set(false);

                if (!response.isSuccessful()) {
                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                ui.isSmsVerification.set(true);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });
    }

    private void getResetTokenWithSms() {
        ui.isLoading.set(true);
        validationError.clear();

        SmsRequest data = new SmsRequest(smsRequestData);

        resetService.getTokenFromSmsRequest(data).enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResetResponse> call, @NonNull Response<ResetResponse> response) {
                if (!response.isSuccessful()) {
                    ui.isLoading.set(false);

                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                ResetResponse resetResponse = response.body();

                if (resetResponse != null)
                    startPasswordChangeActivity(
                            resetResponse.getUserId(), resetResponse.getToken());

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ResetResponse> call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });
    }

    private void startEmailPasswordResetRequest() {
        ui.isLoading.set(true);
        validationError.clear();

        EmailRequest data = new EmailRequest(emailRequestData);

        resetService.getTokenFromEmailRequest(data).enqueue(new Callback<ResetResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResetResponse> call, @NonNull Response<ResetResponse> response) {
                if (!response.isSuccessful()) {
                    ui.isLoading.set(false);

                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                ResetResponse resetResponse = response.body();

                if (resetResponse != null)
                    startPasswordChangeActivity(resetResponse.getUserId(), null);

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ResetResponse> call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });
    }

    private void startPasswordChangeActivity(String userId, String token) {
        Intent intent = new Intent(PasswordResetActivity.this,
                PasswordResetChangeActivity.class);

        if (userId != null)
            intent.putExtra("user_id", userId);

        if (token !=  null)
            intent.putExtra("token", token);

        startActivity(intent);

        overridePendingTransition(
                R.anim.slide_from_right, R.anim.zoom_out);
    }

    public class UiData extends BaseObservable {

        public ObservableInt activeTab = new ObservableInt(RESET_SMS);

        public ObservableBoolean isLoading = new ObservableBoolean(false);

        public ObservableBoolean isSmsVerification = new ObservableBoolean(false);

        @Bindable({"isLoading"})
        public boolean isFormEnabled() {
            return !isLoading.get();
        }

    }
}
