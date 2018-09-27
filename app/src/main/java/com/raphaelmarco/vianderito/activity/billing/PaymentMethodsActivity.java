package com.raphaelmarco.vianderito.activity.billing;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.adapter.PaymentMethodListAdapter;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.databinding.ActivityPaymentMethodsBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.gateway.Customer;
import com.raphaelmarco.vianderito.network.model.gateway.Token;
import com.raphaelmarco.vianderito.network.service.gateway.ClientService;
import com.raphaelmarco.vianderito.network.service.gateway.CustomerService;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodsActivity extends AppCompatActivity {

    private static final int ADD_PAYMENT_METHOD = 5000;

    private static final int VIEW_PAYMENT_METHOD = 5001;

    private UiData ui = new UiData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private PaymentMethodListAdapter adapter;

    private ClientService clientService;

    private CustomerService customerService;

    private String clientToken;

    private boolean invokedFromPaymentUi = false;

    public PaymentMethodsActivity() {
        clientService = RetrofitClient.getInstance().create(ClientService.class);

        customerService = RetrofitClient.getInstance().create(CustomerService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPaymentMethodsBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_payment_methods);

        binding.setUi(ui);
        binding.setValidationError(validationError);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.item_add_payment_method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ui.isTokenLoading.get()) addPaymentMethod();
            }
        });

        RecyclerView paymentMethodList = findViewById(R.id.payment_methods);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        paymentMethodList.setLayoutManager(linearLayoutManager);

        paymentMethodList.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.product_spacing)
        ));

        adapter = new PaymentMethodListAdapter(this);
        adapter.setOnMenuClickListener(new OnPaymentMethodMenuClickListener());

        paymentMethodList.setAdapter(adapter);

        getPaymentMethods();
        obtainClientToken();

        if (getCallingActivity() != null) {
            if (PayActivity.class.getName().equals(getCallingActivity().getClassName())) {
                invokedFromPaymentUi = true;
            }
        }
    }

    private void getPaymentMethods() {
        ui.isListLoading.set(true);

        customerService.get().enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ArrayList<Customer>> call,
                    @NonNull Response<ArrayList<Customer>> response) {

                ui.isListLoading.set(false);

                adapter.setData(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Customer>> call, @NonNull Throwable t) {
                t.printStackTrace();

                ui.isListLoading.set(false);
            }
        });
    }

    private void obtainClientToken() {
        ui.isTokenLoading.set(true);

        clientService.obtainToken().enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                clientToken = response.body().getToken();

                ui.isTokenLoading.set(false);
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                ui.isTokenLoading.set(false);

                showClientTokenError();
            }
        });
    }

    private void showClientTokenError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_methods)
                .setMessage(R.string.client_token_obtain_fail)
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void addPaymentMethod() {
        Intent intent = new Intent(this, AddPaymentMethodActivity.class);

        intent.putExtra("client_token", clientToken);

        startActivityForResult(intent, ADD_PAYMENT_METHOD);

        overridePendingTransition(R.anim.slide_from_right, R.anim.zoom_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (invokedFromPaymentUi) {
            setResult(Activity.RESULT_OK);

            finish();

            return;
        }

        if (requestCode == ADD_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            Alerter.create(this)
                    .setBackgroundColorRes(R.color.green)
                    .setText(R.string.credit_card_added)
                    .show();

            getPaymentMethods();
        }

        if (requestCode == VIEW_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            getPaymentMethods();
        }
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

    public class OnPaymentMethodMenuClickListener
            implements PaymentMethodListAdapter.OnMenuClickListener {

        @Override
        public void onClick(View view, Customer customer) {
            Intent intent = new Intent(getApplicationContext(), ViewPaymentMethodActivity.class);

            intent.putExtra("id", customer.getId());
            intent.putExtra("last_four", customer.getLastFour());
            intent.putExtra("expiration_month", customer.getExpirationMonth());
            intent.putExtra("expiration_year", customer.getExpirationYear());

            startActivityForResult(intent, VIEW_PAYMENT_METHOD);

            overridePendingTransition(R.anim.slide_from_right, R.anim.zoom_out);
        }
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isTokenLoading = new ObservableBoolean(false);

        public ObservableBoolean isListLoading = new ObservableBoolean(false);

        @Bindable({"isTokenLoading", "isListLoading"})
        public boolean getIsLoading() {
            return isTokenLoading.get() || isListLoading.get();
        }

    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.top = space;
        }
    }
}
