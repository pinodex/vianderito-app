package com.raphaelmarco.vianderito.activity.billing;

import android.app.Activity;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.cardform.view.CardForm;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.binding.ValidationErrorData;
import com.raphaelmarco.vianderito.binding.billing.CustomerData;
import com.raphaelmarco.vianderito.databinding.ActivityAddPaymentMethodBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.ValidationError;
import com.raphaelmarco.vianderito.network.model.gateway.Customer;
import com.raphaelmarco.vianderito.network.model.gateway.PaymentMethod;
import com.raphaelmarco.vianderito.network.service.gateway.CustomerService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPaymentMethodActivity extends AppCompatActivity implements
        PaymentMethodNonceCreatedListener, BraintreeErrorListener {

    private UiData ui = new UiData();

    private CustomerData customerData = new CustomerData();

    private ValidationErrorData validationError = new ValidationErrorData();

    private PaymentMethod data = new PaymentMethod();

    private BraintreeFragment braintreeFragment;

    private CustomerService customerService;

    private CardForm cardForm;

    private CardBuilder cardBuilder;

    private String clientToken;

    public AddPaymentMethodActivity() {
        customerService = RetrofitClient.getInstance().create(CustomerService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAddPaymentMethodBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_add_payment_method
        );

        binding.setUi(ui);
        binding.setValidationError(validationError);
        binding.setModel(customerData);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        clientToken = getIntent().getStringExtra("client_token");

        try {
            braintreeFragment = BraintreeFragment.newInstance(this, clientToken);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();

            finish();
        }

        cardForm = findViewById(R.id.card_form);

        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .maskCvv(true)
                .setup(this);

        findViewById(R.id.btn_add_payment_method).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildCard();

                tokenizeCard();
            }
        });
    }

    private void buildCard() {
        String cardNumber = cardForm.getCardNumber();
        String expDate = String.format("%s/%s",
                cardForm.getExpirationMonth(), cardForm.getExpirationYear());

        cardBuilder = new CardBuilder()
                .cardNumber(cardNumber)
                .expirationDate(expDate);

        data.setLastFour(cardNumber.substring(cardNumber.length() - 4));

        data.setExpirationMonth(cardForm.getExpirationMonth());

        data.setExpirationYear(cardForm.getExpirationYear());
    }

    private void tokenizeCard() {
        ui.isLoading.set(true);

        Card.tokenize(braintreeFragment, cardBuilder);
    }

    private void saveChanges() {
        ui.isLoading.set(true);

        data.fill(customerData);

        customerService.create(data).enqueue(new Callback<Customer>() {
            @Override
            public void onResponse(
                    @NonNull Call<Customer> call, @NonNull Response<Customer> response) {

                if (!response.isSuccessful()) {
                    ui.isLoading.set(false);

                    validationError.fill(new ValidationError.Parser(response).parse());

                    return;
                }

                setResult(Activity.RESULT_OK);

                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Customer> call, @NonNull Throwable t) {
                ui.isLoading.set(false);

                t.printStackTrace();
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

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        data.setNonce(paymentMethodNonce.getNonce());
        data.setType(paymentMethodNonce.getTypeLabel());

        saveChanges();
    }

    @Override
    public void onError(Exception error) {
        ui.isLoading.set(false);

        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;

            BraintreeError cardError = errorWithResponse.errorFor("creditCard");
            BraintreeError expirationError = errorWithResponse.errorFor("expirationMonth");

            if (cardError != null) {
                cardForm.setCardNumberError(getString(R.string.credit_card_number_error));
            }

            if (expirationError != null) {
                cardForm.setExpirationError(getString(R.string.credit_card_expiration_error));
            }
        }
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isLoading = new ObservableBoolean(false);

    }
}
