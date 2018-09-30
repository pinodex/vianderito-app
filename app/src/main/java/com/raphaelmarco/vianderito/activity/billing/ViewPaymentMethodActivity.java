package com.raphaelmarco.vianderito.activity.billing;

import android.app.Activity;
import android.content.DialogInterface;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.activity.AuthenticatedActivity;
import com.raphaelmarco.vianderito.databinding.ActivityViewPaymentMethodBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.gateway.PaymentMethod;
import com.raphaelmarco.vianderito.network.service.gateway.CustomerService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewPaymentMethodActivity extends AuthenticatedActivity {

    private UiData ui = new UiData();

    private CustomerService customerService;

    private String id;

    private ActivityViewPaymentMethodBinding binding;

    public ViewPaymentMethodActivity() {
        customerService = RetrofitClient.getInstance().create(CustomerService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_view_payment_method);

        binding.setUi(ui);

        id = getIntent().getStringExtra("id");

        String lastFour = getIntent().getStringExtra("last_four");
        String expirationMonth = getIntent().getStringExtra("expiration_month");
        String expirationYear = getIntent().getStringExtra("expiration_year");

        ui.cardNumber.set("**** **** **** " + lastFour);
        ui.expirationMonth.set(expirationMonth);
        ui.expirationYear.set(expirationYear);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.item_delete_payment_method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        loadPaymentMethod();
    }

    private void loadPaymentMethod() {
        ui.isLoading.set(true);

        customerService.getDetails(id).enqueue(new Callback<PaymentMethod>() {
            @Override
            public void onResponse(
                    @NonNull Call<PaymentMethod> call, @NonNull Response<PaymentMethod> response) {

                binding.setModel(response.body());
                ui.isLoading.set(false);
            }

            @Override
            public void onFailure(@NonNull Call<PaymentMethod> call, @NonNull Throwable t) {
                ui.isLoading.set(false);

                t.printStackTrace();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_payment_method)
                .setMessage(R.string.delete_payment_method_confirm_message)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePaymentMethod();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }

    private void deletePaymentMethod() {
        ui.isLoading.set(true);

        customerService.delete(id).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(
                    @NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                setResult(Activity.RESULT_OK);

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                t.printStackTrace();

                setResult(Activity.RESULT_CANCELED);

                finish();
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

        public ObservableField<String> cardNumber = new ObservableField<>();

        public ObservableField<String> expirationMonth = new ObservableField<>();

        public ObservableField<String> expirationYear = new ObservableField<>();

    }
}
