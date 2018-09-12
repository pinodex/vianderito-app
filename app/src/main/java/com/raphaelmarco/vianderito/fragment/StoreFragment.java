package com.raphaelmarco.vianderito.fragment;

import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.adapter.ProductAdapter;
import com.raphaelmarco.vianderito.databinding.FragmentStoreBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.Paginated;
import com.raphaelmarco.vianderito.network.model.store.Category;
import com.raphaelmarco.vianderito.network.model.store.Product;
import com.raphaelmarco.vianderito.network.service.StoreService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreFragment extends Fragment {

    private UiData ui = new UiData();

    private ArrayList<Product> products;

    private StoreService storeService;

    private ProductAdapter productAdapter;

    private TabLayout categoryTabs;

    private AutoCompleteTextView searchAutocomplete;

    private String productNameQuery = null;

    private String categoryIdQuery = null;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        categoryTabs = view.findViewById(R.id.category_tab);
        searchAutocomplete = view.findViewById(R.id.search_autocomplete);

        categoryTabs.addOnTabSelectedListener(new CategoryTabSelectedListener());

        RxTextView
                .textChanges(searchAutocomplete)
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribe(new OnSearchFieldInput());

        loadProducts(currentPage,false);
        loadCategories();

        loadAutocompleteList();
    }

    private void loadAutocompleteList() {
        storeService.getProductNames().enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call,
                                   @NonNull Response<ArrayList<String>> response) {

                ArrayList<String> names = response.body();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_dropdown_item_1line, names);

                searchAutocomplete.setAdapter(adapter);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {

            }
        });
    }

    private void loadProducts(int page, final boolean append) {
        ui.isProductsLoading.set(true);

        storeService
                .getProducts(page, 10, productNameQuery, null, categoryIdQuery, null)
                .enqueue(new Callback<Paginated<Product>>() {
                    @Override
                    public void onResponse(@NonNull Call<Paginated<Product>> call,
                                           @NonNull Response<Paginated<Product>> response) {

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
                    public void onFailure(@NonNull Call<Paginated<Product>> call,
                                          @NonNull Throwable t) {
                        ui.isProductsLoading.set(false);
                    }
                });
    }

    private void loadCategories() {
        storeService.getCategories().enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<Category>> call,
                                   @NonNull Response<ArrayList<Category>> response) {

                ArrayList<Category> categories = response.body();

                for (Category category : categories) {
                    if (category.getProductCount() == 0) continue;

                    TabLayout.Tab tab = categoryTabs.newTab()
                            .setTag(category.getId())
                            .setText(category.getName());

                    categoryTabs.addTab(tab);
                }

                ui.isCategoriesLoaded.set(true);
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<Category>> call, @NonNull Throwable t) {

            }
        });
    }

    public void disableSearchMode() {
        productNameQuery = null;

        ui.isSearchMode.set(false);
    }

    public void toggleSearchMode() {
        if (ui.isSearchMode.get()) {
            disableSearchMode();

            return;
        }

        ui.isSearchMode.set(true);
    }

    public class UiData extends BaseObservable {

        public ObservableBoolean isProductsLoading = new ObservableBoolean();

        public ObservableBoolean isCategoriesLoaded = new ObservableBoolean();

        public ObservableBoolean isSearchMode = new ObservableBoolean();

        public UiData() {
            isProductsLoading.set(false);
            isCategoriesLoaded.set(false);
            isSearchMode.set(false);
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

    private class CategoryTabSelectedListener implements TabLayout.OnTabSelectedListener {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            categoryIdQuery = null;

            if (tab.getTag() != null) {
                categoryIdQuery = tab.getTag().toString();
            }

            loadProducts(1, false);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) { }

        @Override
        public void onTabReselected(TabLayout.Tab tab) { }
    }

    private class OnSearchFieldInput implements Consumer<CharSequence> {

        @Override
        public void accept(CharSequence charSequence) throws Exception {
            categoryIdQuery = null;
            productNameQuery = charSequence.toString();

            loadProducts(1, false);
        }
    }

}
