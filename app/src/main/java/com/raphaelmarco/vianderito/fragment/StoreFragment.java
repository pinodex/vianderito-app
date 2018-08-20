package com.raphaelmarco.vianderito.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.adapter.ProductGridAdapter;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.Paginated;
import com.raphaelmarco.vianderito.network.model.store.Product;
import com.raphaelmarco.vianderito.network.service.StoreService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends Fragment {

    private ArrayList<Product> products;

    private StoreService storeService;

    private GridView productGrid;

    private ProductGridAdapter productGridAdapter;

    public StoreFragment() {
        products = new ArrayList<>();

        storeService = RetrofitClient.getInstance().create(StoreService.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productGrid = view.findViewById(R.id.product_grid);
        productGridAdapter = new ProductGridAdapter(getContext(), products);

        productGrid.setAdapter(productGridAdapter);

        loadProducts(false);
    }

    private void loadProducts(final boolean append) {
        storeService.getProducts(1).enqueue(new Callback<Paginated<Product>>() {
            @Override
            public void onResponse(Call<Paginated<Product>> call,
                                   Response<Paginated<Product>> response) {

                if (response.body().getData() == null) {
                    return;
                }

                if (!append) products.clear();

                products.addAll(response.body().getData());

                invokeDataSetChanged();
            }

            @Override
            public void onFailure(Call<Paginated<Product>> call, Throwable t) {

            }
        });
    }

    private void invokeDataSetChanged() {
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                productGridAdapter.notifyDataSetChanged();
            }
        };

        handler.post(runnable);
    }

}
