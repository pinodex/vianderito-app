package com.raphaelmarco.vianderito.activity.order;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.activity.AuthenticatedActivity;
import com.raphaelmarco.vianderito.adapter.OrderViewAdapter;
import com.raphaelmarco.vianderito.databinding.ActivityOrderViewBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.cart.Purchase;
import com.raphaelmarco.vianderito.network.service.OrderService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderViewActivity extends AuthenticatedActivity {

    private UiData ui = new UiData();

    private OrderService orderService;

    private ActivityOrderViewBinding binding;

    private OrderViewAdapter adapter;

    private String id;

    public OrderViewActivity() {
        orderService = RetrofitClient.getInstance().create(OrderService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(
                this, R.layout.activity_order_view
        );

        binding.setUi(ui);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id = getIntent().getStringExtra("id");

        RecyclerView productsView = findViewById(R.id.products);

        productsView.setLayoutManager(new LinearLayoutManager(this));

        productsView.addItemDecoration(
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adapter = new OrderViewAdapter();

        productsView.setAdapter(adapter);

        loadOrder();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            overridePendingTransition(R.anim.zoom_in, R.anim.slide_to_right);
        }
    }

    private void loadOrder() {
        ui.isLoading.set(true);

        orderService.getPurchase(id).enqueue(new Callback<Purchase>() {
            @Override
            public void onResponse(
                    @NonNull Call<Purchase> call, @NonNull Response<Purchase> response) {

                Purchase purchase = response.body();

                if (purchase != null) {
                    binding.setModel(purchase);

                    adapter.setData(purchase.products);
                    adapter.notifyDataSetChanged();
                }

                ui.isLoading.set(false);
            }

            @Override
            public void onFailure(@NonNull Call<Purchase> call, @NonNull Throwable t) {
                ui.isLoading.set(false);

                t.printStackTrace();
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

        public ObservableBoolean isLoading = new ObservableBoolean();

    }
}
