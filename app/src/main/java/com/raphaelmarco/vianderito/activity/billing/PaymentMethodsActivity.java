package com.raphaelmarco.vianderito.activity.billing;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
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

    private UiData ui = new UiData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private PaymentMethodListAdapter adapter;

    private ClientService clientService;

    private CustomerService customerService;

    private String clientToken;

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
                if (!ui.isLoading.get()) addPaymentMethod();
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
    }

    private void getPaymentMethods() {
        customerService.get().enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ArrayList<Customer>> call,
                    @NonNull Response<ArrayList<Customer>> response) {

                adapter.setData(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Customer>> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void obtainClientToken() {
        ui.isLoading.set(true);

        clientService.obtainToken().enqueue(new Callback<Token>() {
            @Override
            public void onResponse(@NonNull Call<Token> call, @NonNull Response<Token> response) {
                clientToken = response.body().getToken();

                ui.isLoading.set(false);
            }

            @Override
            public void onFailure(@NonNull Call<Token> call, @NonNull Throwable t) {
                ui.isLoading.set(false);

                new AlertDialog.Builder(getApplicationContext())
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
        });
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

        if (requestCode == ADD_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            Alerter.create(this)
                    .setBackgroundColorRes(R.color.green)
                    .setText(R.string.credit_card_added)
                    .show();

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
        public void onClick(Customer customer) {
            customer.getId();
        }
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isLoading = new ObservableBoolean(false);

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
