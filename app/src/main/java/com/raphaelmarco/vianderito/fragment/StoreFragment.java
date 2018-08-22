package com.raphaelmarco.vianderito.fragment;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.adapter.ProductAdapter;
import com.raphaelmarco.vianderito.databinding.FragmentStoreBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.Paginated;
import com.raphaelmarco.vianderito.network.model.store.Product;
import com.raphaelmarco.vianderito.network.service.StoreService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends Fragment {

    private UiData ui = new UiData();

    private ArrayList<Product> products;

    private StoreService storeService;

    private ProductAdapter productAdapter;

    private int currentPage = 1;

    private int lastPage = 1;

    public StoreFragment() {
        products = new ArrayList<>();

        storeService = RetrofitClient.getInstance().create(StoreService.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        productAdapter = new ProductAdapter(products);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FragmentStoreBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_store,
                container, false);

        View view = binding.getRoot();

        binding.setUi(ui);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        RecyclerView recyclerView = view.findViewById(R.id.product_recycler);

        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productAdapter);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.product_spacing)
        ));

        recyclerView.addOnScrollListener(new ScrollListener());

        loadProducts(currentPage,false);
    }

    private void loadProducts(int page, final boolean append) {
        ui.isProductsLoading.set(true);

        storeService.getProducts(page, 10).enqueue(new Callback<Paginated<Product>>() {
            @Override
            public void onResponse(Call<Paginated<Product>> call,
                                   Response<Paginated<Product>> response) {
                ui.isProductsLoading.set(false);

                currentPage = response.body().getCurrentPage();
                lastPage = response.body().getLastPage();

                if (response.body().getData() == null) {
                    return;
                }

                if (!append) products.clear();

                products.addAll(response.body().getData());
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Paginated<Product>> call, Throwable t) {
                ui.isProductsLoading.set(false);
            }
        });
    }

    public class UiData extends BaseObservable {

        public ObservableField<Boolean> isProductsLoading = new ObservableField<>();

        public UiData() {
            isProductsLoading.set(false);
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
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
        }
    }

    private class ScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            if (!recyclerView.canScrollVertically(1)) {
                if (ui.isProductsLoading.get()) {
                    return;
                }

                if (currentPage >= lastPage) {
                    return;
                }

                loadProducts(currentPage + 1,true);
            }
        }
    }

}
