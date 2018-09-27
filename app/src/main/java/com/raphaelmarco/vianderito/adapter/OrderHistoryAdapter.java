package com.raphaelmarco.vianderito.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewPurchaseEntryBinding;
import com.raphaelmarco.vianderito.network.model.cart.Purchase;

import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private ArrayList<Purchase> data = new ArrayList<>();

    private OnClickListener onClickListener;

    public OrderHistoryAdapter() { }

    public ArrayList<Purchase> getData() {
        return data;
    }

    public void setData(ArrayList<Purchase> data) {
        this.data = data;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ViewPurchaseEntryBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.view_purchase_entry, parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Purchase model = data.get(position);

        holder.bind(model, onClickListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewPurchaseEntryBinding binding;

        ViewHolder(ViewPurchaseEntryBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void bind(final Purchase model, final OnClickListener onClickListener) {
            binding.setModel(model);
            binding.executePendingBindings();

            binding.getRoot().findViewById(R.id.entry)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (onClickListener != null)
                                onClickListener.onClick(view, model);
                        }
                    });
        }
    }

    public interface OnClickListener {
        void onClick(View view, Purchase purchase);
    }
}
