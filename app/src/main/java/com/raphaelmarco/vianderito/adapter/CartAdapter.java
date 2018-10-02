package com.raphaelmarco.vianderito.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewCartItemBinding;
import com.raphaelmarco.vianderito.network.model.store.Inventory;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private ArrayList<Inventory> data = new ArrayList<>();

    public CartAdapter() { }

    public ArrayList<Inventory> getData() {
        return data;
    }

    public void setData(ArrayList<Inventory> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ViewCartItemBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.view_cart_item, parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inventory model = data.get(position);

        holder.bind(model);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Inventory getItem(int index) {
        return data.get(index);
    }

    public void removeItem(int index) {
        data.remove(index);

        notifyItemRemoved(index);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewCartItemBinding binding;

        private RelativeLayout background, foreground;

        ViewHolder(ViewCartItemBinding binding) {
            super(binding.getRoot());

            background = binding.getRoot().findViewById(R.id.background);
            foreground = binding.getRoot().findViewById(R.id.foreground);

            this.binding = binding;
        }

        void bind(Inventory model) {
            binding.setModel(model);

            binding.executePendingBindings();
        }

        public RelativeLayout getBackground() {
            return background;
        }

        public RelativeLayout getForeground() {
            return foreground;
        }
    }
}
