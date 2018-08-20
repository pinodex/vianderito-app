package com.raphaelmarco.vianderito.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewProductBinding;
import com.raphaelmarco.vianderito.network.model.store.Product;

import java.util.ArrayList;

public class ProductGridAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<Product> products;

    public ProductGridAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Product getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewProductBinding binding;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_product, null);

            binding = DataBindingUtil.bind(view);

            view.setTag(binding);
        } else {
            binding = (ViewProductBinding) view.getTag();
        }

        binding.setModel(getItem(i));

        return binding.getRoot();
    }
}
