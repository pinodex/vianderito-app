package com.raphaelmarco.vianderito.activity.order;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.adapter.OrderHistoryAdapter;
import com.raphaelmarco.vianderito.databinding.ActivityOrderHistoryBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.cart.Purchase;
import com.raphaelmarco.vianderito.network.service.OrderService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private UiData ui = new UiData();

    private OrderService orderService;

    private OrderHistoryAdapter adapter;

    public OrderHistoryActivity() {
        orderService = RetrofitClient.getInstance().create(OrderService.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityOrderHistoryBinding binding = DataBindingUtil.setContentView(
                this, R.layout.activity_order_history
        );

        binding.setUi(ui);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView orderHistory = findViewById(R.id.order_history);

        orderHistory.setLayoutManager(new LinearLayoutManager(this));

        adapter = new OrderHistoryAdapter();
        adapter.setOnClickListener(new OrderHistoryAdapter.OnClickListener() {
            @Override
            public void onClick(View view, Purchase purchase) {
                openOrderHistory(purchase);
            }
        });

        orderHistory.setAdapter(adapter);

        loadOrderHistory();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isFinishing()) {
            overridePendingTransition(R.anim.zoom_in, R.anim.slide_to_right);
        }
    }

    private void openOrderHistory(Purchase purchase) {
        Intent intent = new Intent(this, OrderViewActivity.class);

        intent.putExtra("id", purchase.id);

        startActivity(intent);

        overridePendingTransition(R.anim.slide_from_right, R.anim.zoom_out);
    }

    private void loadOrderHistory() {
        ui.isLoading.set(true);

        orderService.getOrders().enqueue(new Callback<ArrayList<Purchase>>() {
            @Override
            public void onResponse(
                    @NonNull Call<ArrayList<Purchase>> call,
                    @NonNull Response<ArrayList<Purchase>> response) {
                ui.isLoading.set(false);

                ArrayList<Purchase> purchases = response.body();

                adapter.setData(purchases);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Purchase>> call, @NonNull Throwable t) {
                t.printStackTrace();

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

        public ObservableBoolean isLoading = new ObservableBoolean();

    }
}
