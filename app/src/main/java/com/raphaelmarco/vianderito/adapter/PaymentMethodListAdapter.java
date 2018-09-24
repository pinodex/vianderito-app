package com.raphaelmarco.vianderito.adapter;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.raphaelmarco.vianderito.R;
import com.raphaelmarco.vianderito.databinding.ViewPaymentMethodBinding;
import com.raphaelmarco.vianderito.network.model.gateway.Customer;

import java.util.ArrayList;

public class PaymentMethodListAdapter extends
        RecyclerView.Adapter<PaymentMethodListAdapter.ViewHolder> {

    private Context context;

    private ArrayList<Customer> data = new ArrayList<>();

    private OnMenuClickListener onMenuClickListener;

    private boolean useChevronIcon = false;

    public PaymentMethodListAdapter(Context context) {
        this.context = context;
    }

    public PaymentMethodListAdapter(Context context, boolean useChevronIcon) {
        this.context = context;

        this.useChevronIcon = useChevronIcon;
    }

    public ArrayList<Customer> getData() {
        return data;
    }

    public void setData(ArrayList<Customer> data) {
        this.data = data;
    }

    public void setOnMenuClickListener(OnMenuClickListener onMenuClickListener) {
        this.onMenuClickListener = onMenuClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        ViewPaymentMethodBinding binding = DataBindingUtil.inflate(
                layoutInflater, R.layout.view_payment_method, parent,false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer model = data.get(position);

        holder.bind(context, model, useChevronIcon, onMenuClickListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewPaymentMethodBinding binding;

        ViewHolder(ViewPaymentMethodBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void bind(Context context, final Customer model, boolean useChevronIcon,
                  final OnMenuClickListener onMenuClickListener) {

            UiData ui = new UiData(context, model, useChevronIcon);
            ImageView menuButton = binding.getRoot().findViewById(R.id.payment_method_menu);

            binding.setModel(model);
            binding.setUi(ui);

            binding.executePendingBindings();

            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onMenuClickListener != null)
                        onMenuClickListener.onClick(view, model);
                }
            });
        }
    }

    public interface OnMenuClickListener {
        void onClick(View view, Customer customer);
    }

    public static class UiData extends BaseObservable {

        private Context context;

        private Customer model;

        private boolean useChevronIcon;

        public UiData(Context context, Customer model, boolean useChevronIcon) {
            this.context = context;
            this.model = model;
            this.useChevronIcon = useChevronIcon;
        }

        public Drawable getCardIcon() {
            int resId = R.drawable.ic_credit_card;

            switch (model.getType()) {
                case "American Express": resId = R.drawable.ic_mastercard_logo; break;
                case "Visa": resId = R.drawable.ic_visa_logo; break;
            }

            return context.getResources().getDrawable(resId);
        }

        public boolean getIsChevronIcon() {
            return useChevronIcon;
        }

    }
}
