package com.raphaelmarco.vianderito.activity;

import android.app.Activity;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.ActivityProfileEditBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.auth.User;
import com.raphaelmarco.vianderito.network.service.AuthService;
import com.raphaelmarco.vianderito.network.service.ProfileService;

import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileEditActivity extends AppCompatActivity {

    private UiData ui = new UiData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private AuthService authService;

    private ProfileService profileService;

    public ProfileEditActivity() {
        authService = RetrofitClient.getInstance().create(AuthService.class);
        profileService = RetrofitClient.getInstance().create(ProfileService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityProfileEditBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_profile_edit);

        binding.setUi(ui);
        binding.setValidationError(validationError);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ui.isLoading.set(true);

        authService.me().enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                ui.isLoading.set(false);

                ui.user.set(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                ui.isLoading.set(false);
            }
        });
    }

    private void saveChanges() {
        ui.isLoading.set(true);

        User user = ui.user.get();

        profileService.save(user).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                ui.isLoading.set(false);

                if (response.code() == 422) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_edit_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.save:
                saveChanges();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class UiData extends BaseObservable {

        public ObservableField<User> user = new ObservableField<>();

        public ObservableBoolean isLoading = new ObservableBoolean();

    }
}
