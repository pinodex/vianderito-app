package com.raphaelmarco.vianderito.activity.password;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.activity.AuthenticatedActivity;
import com.raphaelmarco.vianderito.binding.PasswordChangeData;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.ActivityChangePasswordBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.auth.PasswordChange;
import com.raphaelmarco.vianderito.network.service.AuthService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AuthenticatedActivity {

    private UiData ui = new UiData();

    private PasswordChangeData passwordChange = new PasswordChangeData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private AuthService authService;

    public ChangePasswordActivity() {
        authService = RetrofitClient.getInstance().create(AuthService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityChangePasswordBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_change_password);

        binding.setUi(ui);
        binding.setPassword(passwordChange);
        binding.setValidationError(validationError);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.btn_change_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveChanges();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            overridePendingTransition(R.anim.zoom_in, R.anim.slide_to_right);
        }
    }

    private void saveChanges() {
        ui.isLoading.set(true);
        validationError.clear();

        PasswordChange data = new PasswordChange(passwordChange);

        authService.changePassword(data).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    ui.isLoading.set(false);

                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                setResult(Activity.RESULT_OK);

                finish();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isLoading = new ObservableBoolean(false);

    }
}
