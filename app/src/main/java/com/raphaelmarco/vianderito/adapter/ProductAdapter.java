package com.raphaelmarco.vianderito.adapter;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewProductBinding;
import com.raphaelmarco.vianderito.network.RetrofitClient;
import com.raphaelmarco.vianderito.network.model.Paginated;
import com.raphaelmarco.vianderito.network.model.store.Product;
import com.raphaelmarco.vianderito.network.service.StoreService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductAdapter extends PagedListAdapter<Product, ProductAdapter.ViewHolder> {

    private ArrayList<Product> products;

    public ProductAdapter(ArrayList<Product> products) {
        super(DIFF_CALLBACK);

        this.products = products;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ViewProductBinding binding = DataBindingUtil
                .inflate(layoutInflater, R.layout.view_product, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public void submitList(PagedList<Product> pagedList) {
        super.submitList(pagedList);

        products.addAll(pagedList);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewProductBinding binding;

        ViewHolder(ViewProductBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void bind(Product model) {
            binding.setModel(model);

            binding.executePendingBindings();
        }
    }

    public static class DataSource extends PageKeyedDataSource<Integer, Product> {
        private StoreService storeService;

        private int page = 1;

        public DataSource() {
            storeService = RetrofitClient.getInstance().create(StoreService.class);
        }

        @Override
        public void loadInitial(@NonNull LoadInitialParams params, @NonNull final LoadInitialCallback callback) {
            storeService.getProducts(page).enqueue(new Callback<Paginated<Product>>() {
                @Override
                public void onResponse(Call<Paginated<Product>> call,
                                       Response<Paginated<Product>> response) {

                    ArrayList<Product> data = response.body().getData();

                    int position = response.body().getCurrentPage();
                    int totalCount = response.body().getTotal();
                    int previousPage = response.body().getCurrentPage() - 1;
                    int nextPage = response.body().getCurrentPage() + 1;

                    callback.onResult(data, previousPage, nextPage);
                }

                @Override
                public void onFailure(Call<Paginated<Product>> call, Throwable t) {

                }
            });
        }

        @Override
        public void loadBefore(@NonNull LoadParams params, @NonNull LoadCallback callback) {

        }

        @Override
        public void loadAfter(@NonNull LoadParams params, @NonNull LoadCallback callback) {

        }
    }

    public static class DataSourceFactory extends DataSource.Factory<Integer, Product> {
        @Override
        public DataSource create() {
            return new DataSource();
        }
    }

    private static final DiffUtil.ItemCallback<Product> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Product>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Product oldModel, @NonNull Product newModel) {
                    return oldModel.getId().equals(newModel.getId());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Product oldModel, @NonNull Product newModel) {
                    return oldModel.getId().equals(newModel.getId());
                }
            };
}
