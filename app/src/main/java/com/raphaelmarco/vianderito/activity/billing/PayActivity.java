package com.raphaelmarco.vianderito.activity.billing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.activity.AuthenticatedActivity;
import com.raphaelmarco.vianderito.adapter.PaymentMethodListAdapter;
import com.raphaelmarco.vianderito.databinding.ActivityPayBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.cart.Purchase;
import com.raphaelmarco.vianderito.network.model.cart.PurchaseRequest;
import com.raphaelmarco.vianderito.network.model.cart.Transaction;
import com.raphaelmarco.vianderito.network.model.gateway.Customer;
import com.raphaelmarco.vianderito.network.model.gateway.Payment;
import com.raphaelmarco.vianderito.network.service.CartService;
import com.raphaelmarco.vianderito.network.service.gateway.CustomerService;
import com.raphaelmarco.vianderito.network.service.gateway.PaymentService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayActivity extends AuthenticatedActivity {

    public static final int ADD_PAYMENT_METHOD = 3000;

    private UiData ui = new UiData();

    private CartService cartService;

    private CustomerService customerService;

    private PaymentService paymentService;

    private String transactionId;

    private ActivityPayBinding binding;

    private PaymentMethodListAdapter adapter;

    public PayActivity() {
        cartService = RetrofitClient.getInstance().create(CartService.class);

        customerService = RetrofitClient.getInstance().create(CustomerService.class);

        paymentService = RetrofitClient.getInstance().create(PaymentService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_pay);

        binding.setUi(ui);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        transactionId = getIntent().getStringExtra("transaction_id");

        RecyclerView paymentMethodList = findViewById(R.id.payment_methods);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        paymentMethodList.setLayoutManager(linearLayoutManager);

        paymentMethodList.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.product_spacing)
        ));

        adapter = new PaymentMethodListAdapter(this, true);
        adapter.setOnMenuClickListener(new OnPaymentMethodMenuClickListener());

        paymentMethodList.setAdapter(adapter);

        findViewById(R.id.item_add_payment_method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPaymentMethod();
            }
        });

        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTransactionCompleteMessageClose();
            }
        });

        loadTransaction();
        loadPaymentMethods();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            overridePendingTransition(R.anim.zoom_in, R.anim.slide_to_right);
        }
    }

    private void loadPaymentMethods() {
        ui.isPaymentMethodLoading.set(true);
        ui.isPaymentMethodNone.set(false);

        customerService.get().enqueue(new Callback<ArrayList<Customer>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ArrayList<Customer>> call,
                    @NonNull Response<ArrayList<Customer>> response) {

                ui.isPaymentMethodLoading.set(false);

                if (response.body() == null || response.body().isEmpty()) {
                    ui.isPaymentMethodNone.set(true);

                    showNoAvailablePaymentMethod();

                    return;
                }

                adapter.setData(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Customer>> call, @NonNull Throwable t) {
                t.printStackTrace();

                ui.isPaymentMethodLoading.set(false);
            }
        });
    }

    private void loadTransaction() {
        ui.isTransactionLoading.set(true);

        cartService.get(transactionId).enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(
                    @NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {

                if (!response.isSuccessful()) {
                    returnError();

                    return;
                }

                ui.isTransactionLoading.set(false);

                if (response.body() != null) {
                    binding.setTransaction(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {
                t.printStackTrace();

                returnError();
            }
        });
    }

    private void createPayment(Customer customer) {
        ui.isTransactionLoading.set(true);

        Payment data = new Payment();

        data.customerId = customer.getId();
        data.amount = binding.getTransaction().total;

        paymentService.create(data).enqueue(new Callback<Payment>() {
            @Override
            public void onResponse(@NonNull Call<Payment> call, @NonNull Response<Payment> response) {
                ui.isTransactionLoading.set(false);

                if (!response.isSuccessful()) {
                    ValidationError validationError = new ValidationError.Parser(response).parse();

                    showErrorMessage(validationError.getMessage());

                    return;
                }

                invokePayment(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<Payment> call, @NonNull Throwable t) {
                t.printStackTrace();

                ui.isTransactionLoading.set(false);

                showErrorMessage(getString(R.string.payment_error));
            }
        });
    }

    private void invokePayment(Payment payment) {
        ui.isTransactionLoading.set(true);

        PurchaseRequest purchaseRequest = new PurchaseRequest(payment);

        cartService.purchase(transactionId, purchaseRequest).enqueue(new Callback<Purchase>() {
            @Override
            public void onResponse(@NonNull Call<Purchase> call, @NonNull Response<Purchase> response) {
                ui.isTransactionLoading.set(false);

                if (!response.isSuccessful()) {
                    ValidationError validationError = new ValidationError.Parser(response).parse();

                    showErrorMessage(validationError.getMessage());

                    return;
                }

                ui.isCompleted.set(true);
            }

            @Override
            public void onFailure(@NonNull Call<Purchase> call, @NonNull Throwable t) {
                t.printStackTrace();

                ui.isTransactionLoading.set(false);

                showErrorMessage(getString(R.string.payment_error));
            }
        });
    }

    private void onTransactionCompleteMessageClose() {
        setResult(Activity.RESULT_OK);

        finish();
    }

    private void showNoAvailablePaymentMethod() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_methods)
                .setMessage(R.string.no_payment_method_available)
                .setPositiveButton(R.string.add_payment_method, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addPaymentMethod();
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

    private void showErrorMessage(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_error)
                .setMessage(message)
                .setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create()
                .show();
    }

    private void addPaymentMethod() {
        Intent intent = new Intent(this, PaymentMethodsActivity.class);

        startActivityForResult(intent, ADD_PAYMENT_METHOD);

        overridePendingTransition(R.anim.slide_from_right, R.anim.zoom_out);
    }

    private void returnError() {
        setResult(Activity.RESULT_CANCELED);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            loadPaymentMethods();
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

        public ObservableBoolean isTransactionLoading = new ObservableBoolean(false);

        public ObservableBoolean isPaymentMethodLoading = new ObservableBoolean(false);

        public ObservableBoolean isCompleted = new ObservableBoolean(false);

        public ObservableBoolean isPaymentMethodNone = new ObservableBoolean(false);

        @Bindable({"isTransactionLoading", "isPaymentMethodLoading"})
        public boolean getIsLoading() {
            return isTransactionLoading.get() || isPaymentMethodLoading.get();
        }

    }

    private class OnPaymentMethodMenuClickListener implements
            PaymentMethodListAdapter.OnMenuClickListener {
        @Override
        public void onClick(View view, final Customer customer) {
            String message = getString(R.string.confirm_payment_from_method,
                    binding.getTransaction().total, customer.getLastFour());

            new AlertDialog.Builder(PayActivity.this)
                    .setTitle(R.string.confirm_payment)
                    .setMessage(message)
                    .setPositiveButton(R.string.pay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            createPayment(customer);
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
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;
        }
    }
}
