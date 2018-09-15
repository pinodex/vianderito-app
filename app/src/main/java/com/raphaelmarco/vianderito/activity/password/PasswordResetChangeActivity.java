package com.raphaelmarco.vianderito.activity.password;

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
import com.raphaelmarco.vianderito.binding.passwordreset.ResetPasswordChangeData;
import com.raphaelmarco.vianderito.databinding.ActivityPasswordResetChangeBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.password.PasswordChange;
import com.raphaelmarco.vianderito.network.service.password.ResetService;
import com.tapadoo.alerter.Alerter;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordResetChangeActivity extends AppCompatActivity {

    public static final int SCREEN_TOKEN = 1;

    public static final int SCREEN_CHANGE_PASSWORD = 2;

    public static final int SCREEN_COMPLETED = 3;

    private UiData ui = new UiData();

    private ResetPasswordChangeData resetPasswordChangeData = new ResetPasswordChangeData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private ResetService resetService;

    private String userId;

    public PasswordResetChangeActivity() {
        resetService = RetrofitClient.getInstance().create(ResetService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPasswordResetChangeBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_password_reset_change
        );

        binding.setUi(ui);
        binding.setValidationError(validationError);
        binding.setReset(resetPasswordChangeData);

        if (getIntent().hasExtra("user_id")) {
            userId = getIntent().getStringExtra("user_id");
        }

        if (getIntent().hasExtra("token")) {
            resetPasswordChangeData.token.set(getIntent().getStringExtra("token"));

            ui.screen.set(SCREEN_CHANGE_PASSWORD);
        }

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ui.screen.set(SCREEN_CHANGE_PASSWORD);
            }
        });

        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassword();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void changePassword() {
        ui.isLoading.set(true);

        PasswordChange data = new PasswordChange(resetPasswordChangeData);

        resetService.changePassword(userId, data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                ui.isLoading.set(false);

                if (!response.isSuccessful()) {
                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                ui.screen.set(SCREEN_COMPLETED);
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isLoading = new ObservableBoolean(false);

        public ObservableInt screen = new ObservableInt(SCREEN_TOKEN);

        @Bindable({"isLoading"})
        public boolean isFormEnabled() {
            return !isLoading.get();
        }

    }
}
