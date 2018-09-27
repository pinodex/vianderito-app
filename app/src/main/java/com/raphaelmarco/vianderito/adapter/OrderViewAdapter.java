package com.raphaelmarco.vianderito.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewPurchaseItemBinding;
import com.raphaelmarco.vianderito.network.model.cart.PurchaseProduct;

import java.util.ArrayList;

public class OrderViewAdapter extends RecyclerView.Adapter<OrderViewAdapter.ViewHolder> {

    private ArrayList<PurchaseProduct> data = new ArrayList<>();

    public OrderViewAdapter() { }

    public ArrayList<PurchaseProduct> getData() {
        return data;
    }

    public void setData(ArrayList<PurchaseProduct> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ViewPurchaseItemBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.view_purchase_item, parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseProduct model = data.get(position);

        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewPurchaseItemBinding binding;

        ViewHolder(ViewPurchaseItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void bind(PurchaseProduct model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }
}
