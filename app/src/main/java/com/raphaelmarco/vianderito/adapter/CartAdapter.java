package com.raphaelmarco.vianderito.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewCartItemBinding;
import com.raphaelmarco.vianderito.network.model.store.Inventory;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;

    private ArrayList<Inventory> data;

    public CartAdapter(Context context) {
        this.context = context;
    }

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

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewCartItemBinding binding;

        public ViewHolder(ViewCartItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void bind(Inventory model) {
            binding.setModel(model);

            binding.executePendingBindings();
        }
    }
}
