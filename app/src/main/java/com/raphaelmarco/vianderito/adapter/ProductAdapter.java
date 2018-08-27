package com.raphaelmarco.vianderito.adapter;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewProductBinding;
import com.raphaelmarco.vianderito.network.model.store.Product;

import java.util.ArrayList;

public class ProductAdapter extends PagedListAdapter<Product, ProductAdapter.ViewHolder> {

    private ArrayList<Product> products;

    public ProductAdapter(ArrayList<Product> products) {
        super(DIFF_CALLBACK);

        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
